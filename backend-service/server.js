const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const https = require('https');
require('dotenv').config();

const app = express();
const PORT = 8080;
const DB_FILE = path.join(__dirname, 'users.json');

app.use(cors());
app.use(bodyParser.json());

// Helper function to read database file
function readDatabase() {
    if (!fs.existsSync(DB_FILE)) {
        fs.writeFileSync(DB_FILE, JSON.stringify({}));
    }
    const data = fs.readFileSync(DB_FILE, 'utf8');
    return JSON.parse(data || '{}');
}

// Helper function to write to database file
function writeDatabase(data) {
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2));
}

// ----------------------------------------------------
// 🔐 AUTHENTICATION ENDPOINTS
// ----------------------------------------------------

// User Sign Up
app.post('/api/auth/signup', (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) {
        return res.status(400).json({ success: false, message: 'Username and password are required' });
    }

    const db = readDatabase();
    if (db[username]) {
        return res.status(400).json({ success: false, message: 'Username already exists' });
    }

    // In a production app, we would hash this password (e.g., using bcrypt)
    db[username] = {
        password: password, // Store password
        tasks: [],
        journal: []
    };
    writeDatabase(db);

    console.log(`[Auth] User registered: ${username}`);
    res.json({ success: true, message: 'Registration successful!' });
});

// User Log In
app.post('/api/auth/login', (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) {
        return res.status(400).json({ success: false, message: 'Username and password are required' });
    }

    const db = readDatabase();
    const user = db[username];

    if (!user || user.password !== password) {
        return res.status(401).json({ success: false, message: 'Invalid username or password' });
    }

    console.log(`[Auth] User logged in: ${username}`);
    res.json({ success: true, username: username });
});

// ----------------------------------------------------
// 📝 TASKS & JOURNAL DATA ENDPOINTS
// ----------------------------------------------------

// Fetch Tasks for a specific user
app.get('/api/tasks', (req, res) => {
    const { username } = req.query;
    if (!username) return res.status(400).json({ error: 'Username parameter is required' });

    const db = readDatabase();
    const user = db[username];
    if (!user) return res.status(404).json({ error: 'User not found' });

    res.json(user.tasks || []);
});

// Update/Save Tasks for a specific user
app.post('/api/tasks', (req, res) => {
    const { username, tasks } = req.body;
    if (!username || !tasks) return res.status(400).json({ error: 'Username and tasks are required' });

    const db = readDatabase();
    if (!db[username]) return res.status(404).json({ error: 'User not found' });

    db[username].tasks = tasks;
    writeDatabase(db);

    console.log(`[Tasks] Updated list for: ${username} (${tasks.length} tasks)`);
    res.json({ success: true });
});

// Fetch Journal Entries for a specific user
app.get('/api/journal', (req, res) => {
    const { username } = req.query;
    if (!username) return res.status(400).json({ error: 'Username parameter is required' });

    const db = readDatabase();
    const user = db[username];
    if (!user) return res.status(404).json({ error: 'User not found' });

    res.json(user.journal || []);
});

// Update/Save Journal for a specific user
app.post('/api/journal', (req, res) => {
    const { username, journal } = req.body;
    if (!username || !journal) return res.status(400).json({ error: 'Username and journal entries are required' });

    const db = readDatabase();
    if (!db[username]) return res.status(404).json({ error: 'User not found' });

    db[username].journal = journal;
    writeDatabase(db);

    console.log(`[Journal] Updated list for: ${username} (${journal.length} entries)`);
    res.json({ success: true });
});

// ----------------------------------------------------
// 🤖 AI ASSISTANT QUERY ROUTER
// ----------------------------------------------------

app.post('/api/ai/query', (req, res) => {
    const { query, model } = req.body;
    if (!query || !model) {
        return res.status(400).json({ error: 'Query and model parameters are required' });
    }

    const isGemini = model.toLowerCase() === 'gemini';
    
    // Check if we have real API keys configured
    const openaiKey = process.env.OPENAI_API_KEY;
    const geminiKey = process.env.GEMINI_API_KEY;

    console.log(`[AI] Query received for: ${model}`);

    if (isGemini && geminiKey) {
        // Call the real Google Gemini API
        callGeminiApi(query, geminiKey, (err, responseText) => {
            if (err) return res.status(500).json({ response: `Error contacting Gemini: ${err.message}` });
            res.json({ response: `[Gemini API - Live] ${responseText}` });
        });
    } else if (!isGemini && openaiKey) {
        // Call the real OpenAI ChatGPT API
        callChatGPTApi(query, openaiKey, (err, responseText) => {
            if (err) return res.status(500).json({ response: `Error contacting ChatGPT: ${err.message}` });
            res.json({ response: `[ChatGPT API - Live] ${responseText}` });
        });
    } else {
        // Fallback to Demo Mode (Mock Responses)
        setTimeout(() => {
            const demoResponse = getMockResponse(query, isGemini);
            res.json({ response: demoResponse });
        }, 1000); // Simulate network latency
    }
});

// Real Gemini API network caller
function callGeminiApi(prompt, apiKey, callback) {
    const postData = JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }]
    });

    const options = {
        hostname: 'generativelanguage.googleapis.com',
        path: `/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': Buffer.byteLength(postData)
        }
    };

    const req = https.request(options, (res) => {
        let body = '';
        res.on('data', (chunk) => body += chunk);
        res.on('end', () => {
            try {
                const parsed = JSON.parse(body);
                const text = parsed.candidates[0].content.parts[0].text;
                callback(null, text);
            } catch (e) {
                callback(new Error('Failed to parse Gemini response'));
            }
        });
    });

    req.on('error', (e) => callback(e));
    req.write(postData);
    req.end();
}

// Real ChatGPT API network caller
function callChatGPTApi(prompt, apiKey, callback) {
    const postData = JSON.stringify({
        model: 'gpt-3.5-turbo',
        messages: [{ role: 'user', content: prompt }]
    });

    const options = {
        hostname: 'api.openai.com',
        path: '/v1/chat/completions',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${apiKey}`,
            'Content-Length': Buffer.byteLength(postData)
        }
    };

    const req = https.request(options, (res) => {
        let body = '';
        res.on('data', (chunk) => body += chunk);
        res.on('end', () => {
            try {
                const parsed = JSON.parse(body);
                const text = parsed.choices[0].message.content;
                callback(null, text);
            } catch (e) {
                callback(new Error('Failed to parse ChatGPT response'));
            }
        });
    });

    req.on('error', (e) => callback(e));
    req.write(postData);
    req.end();
}

// Intelligent Mock Responses for offline Demo Mode
function getMockResponse(query, isGemini) {
    const cleanQuery = query.toLowerCase();
    
    if (isGemini) {
        let reply = "[Gemini - Demo Mode] 🧘 Here are my thoughts based on your prompt:\n\n";
        if (cleanQuery.includes('focus') || cleanQuery.includes('study') || cleanQuery.includes('work')) {
            reply += "• Try the 25-minute Pomodoro method to keep distraction at bay.\n";
            reply += "• Keep your workspace neat and clear your phone notifications.\n";
            reply += "• Breathe deeply for 1 minute before starting your high-priority task.";
        } else if (cleanQuery.includes('stress') || cleanQuery.includes('anxious') || cleanQuery.includes('tired')) {
            reply += "• Take a 5-minute break to stretch or walk outside.\n";
            reply += "• Inhale for 4 seconds, hold for 4, exhale for 4 (box breathing).\n";
            reply += "• Write down whatever is bothering you in your Zen Journal to release it.";
        } else {
            reply += "To keep your day mindful and organized, break your tasks down into tiny, bite-sized goals and check them off one-by-one. What focus target will we tackle next?";
        }
        return reply;
    } else {
        let reply = "[ChatGPT - Demo Mode] 🚀 Glad to help! Here is a quick strategy for you:\n\n";
        if (cleanQuery.includes('focus') || cleanQuery.includes('study') || cleanQuery.includes('work')) {
            reply += "1. Pick your absolute number ONE task from your planner.\n";
            reply += "2. Set a timer for 20 minutes and commit only to that task.\n";
            reply += "3. Give yourself a quick sip of water or a stretch as a reward when done!";
        } else if (cleanQuery.includes('stress') || cleanQuery.includes('anxious') || cleanQuery.includes('tired')) {
            reply += "1. Stop looking at your screen for 2 minutes.\n";
            reply += "2. Acknowledge three things you can see and hear around you right now.\n";
            reply += "3. Remind yourself: progress is about tiny steps, not perfection.";
        } else {
            reply += "Setting clear, small priority targets (Low/Medium/High) is the fastest way to get things done without feeling overwhelmed. Let's add a task and check it off!";
        }
        return reply;
    }
}

// Start Server
app.listen(PORT, () => {
    console.log(`====================================================`);
    console.log(`🚀 MindfulTasks Microservice running on port ${PORT}`);
    console.log(`📂 Database file stored at: ${DB_FILE}`);
    console.log(`====================================================`);
});
