package com.mindful.tasks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindful.tasks.ui.theme.*
import com.mindful.tasks.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val journalPrompt by viewModel.currentJournalPrompt.collectAsState()
    val geminiMessages by viewModel.geminiMessages.collectAsState()
    val chatGptMessages by viewModel.chatGptMessages.collectAsState()
    val selectedAiModel by viewModel.selectedAiModel.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Tasks, 1 = Journal, 2 = AI

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Composable with Logout action
            MindfulHeader(
                username = currentUser,
                onLogout = { viewModel.logout() }
            )

            // Custom Glass-style Tab Selector (3 Tabs)
            TabSelector(activeTab = activeTab, onTabSelected = { activeTab = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Area based on Selected Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                when (activeTab) {
                    0 -> TasksTab(
                        tasks = tasks,
                        onAddTask = { title, priority, cat -> viewModel.addTask(title, priority, cat) },
                        onToggleTask = { viewModel.toggleTask(it) },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )
                    1 -> JournalTab(
                        entries = journalEntries,
                        prompt = journalPrompt,
                        onAddEntry = { text, mood -> viewModel.addJournalEntry(text, mood) }
                    )
                    2 -> AiTab(
                        messages = if (selectedAiModel.lowercase() == "gemini") geminiMessages else chatGptMessages,
                        selectedModel = selectedAiModel,
                        onModelSelected = { viewModel.setAiModel(it) },
                        onSendMessage = { viewModel.sendAiQuery(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun MindfulHeader(username: String?, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "MindfulTasks",
                style = Typography.displayLarge,
                color = EmeraldZen,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Breathe in. Plan. Zen space for ${username ?: "Guest"}.",
                style = Typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Elegant Sign Out button matching glassmorphic borders
        OutlinedButton(
            onClick = onLogout,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LightRose),
            border = BorderStroke(1.dp, BorderGlass),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text("Logout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TabSelector(activeTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DeepCharcoal)
            .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        val tabs = listOf("Tasks", "Journal", "Zen AI")
        tabs.forEachIndexed { index, title ->
            val isSelected = activeTab == index
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) EmeraldZen else Color.Transparent,
                label = "tabBg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) ObsidianBlack else TextSecondary,
                label = "tabText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = Typography.titleLarge.copy(fontSize = 15.sp),
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TasksTab(
    tasks: List<Task>,
    onAddTask: (String, Priority, String) -> Unit,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Focus Today",
                style = Typography.headlineMedium.copy(fontSize = 20.sp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showAddTaskDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldZen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = ObsidianBlack)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Task", color = ObsidianBlack, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("All tasks cleared! Have a mindful moment. 🧘", color = TextTertiary, style = Typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { onToggleTask(task.id) },
                        onDelete = { onDeleteTask(task.id) }
                    )
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, priority, cat ->
                onAddTask(title, priority, cat)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> SunsetAmber
        Priority.MEDIUM -> EmeraldZen
        Priority.LOW -> TextSecondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DeepCharcoal)
            .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = "Toggle",
            tint = if (task.isCompleted) EmeraldZen else TextSecondary,
            modifier = Modifier
                .size(24.dp)
                .clickable { onToggle() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = Typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (task.isCompleted) TextTertiary else TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(priorityColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${task.category} • ${task.priority.name}",
                    style = Typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = LightRose)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, Priority, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Life") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Mindful Task", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What will you focus on?") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldZen,
                        unfocusedBorderColor = BorderGlass,
                        focusedContainerColor = ObsidianBlack,
                        unfocusedContainerColor = ObsidianBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Skill, Health, Mind)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldZen,
                        unfocusedBorderColor = BorderGlass,
                        focusedContainerColor = ObsidianBlack,
                        unfocusedContainerColor = ObsidianBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Priority Level", color = TextSecondary, style = Typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { level ->
                        val isSelected = priority == level
                        val chipBg = if (isSelected) EmeraldZen else DeepCharcoal
                        val chipText = if (isSelected) ObsidianBlack else TextPrimary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(chipBg)
                                .border(1.dp, BorderGlass, RoundedCornerShape(10.dp))
                                .clickable { priority = level }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level.name,
                                color = chipText,
                                fontWeight = FontWeight.Bold,
                                style = Typography.labelSmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, priority, category) },
                colors = ButtonDefaults.textButtonColors(contentColor = EmeraldZen)
            ) {
                Text("Add Focus", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)) {
                Text("Cancel")
            }
        },
        containerColor = DeepCharcoal,
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalTab(
    entries: List<JournalEntry>,
    prompt: String,
    onAddEntry: (String, String) -> Unit
) {
    var journalText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("🧘") }
    val moods = listOf("🧘", "🚀", "😊", "😴", "📝")

    Column(modifier = Modifier.fillMaxSize()) {
        // Daily Reflection Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepCharcoal, ObsidianBlack)
                    )
                )
                .border(1.dp, BorderGlass, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Daily reflection",
                style = Typography.labelSmall,
                color = EmeraldZen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prompt,
                style = Typography.titleLarge.copy(fontSize = 18.sp),
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                placeholder = { Text("Write your thoughts down...", color = TextTertiary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = EmeraldZen,
                    unfocusedBorderColor = BorderGlass,
                    focusedContainerColor = ObsidianBlack,
                    unfocusedContainerColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mood Picker
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    moods.forEach { mood ->
                        val isSelected = selectedMood == mood
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) SurfaceSelected else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldZen else Color.Transparent,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedMood = mood },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mood, fontSize = 18.sp)
                        }
                    }
                }

                Button(
                    onClick = {
                        onAddEntry(journalText, selectedMood)
                        journalText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldZen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Reflect", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your Mind Logs",
            style = Typography.headlineMedium.copy(fontSize = 18.sp),
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(entries, key = { it.id }) { entry ->
                JournalRow(entry = entry)
            }
        }
    }
}

@Composable
fun JournalRow(entry: JournalEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DeepCharcoal)
            .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Text(text = entry.moodEmoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.date,
                style = Typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.text,
                style = Typography.bodyMedium,
                color = TextPrimary,
                lineHeight = 20.sp
            )
        }
    }
}

// ----------------------------------------------------
// 🤖 ZEN AI CHAT COMPOSABLE
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTab(
    messages: List<ChatMessage>,
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    var queryText by remember { mutableStateOf("") }
    val isGeminiActive = selectedModel.lowercase() == "gemini"

    // Colors & Gradients
    val geminiGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4285F4), Color(0xFF9B72CB), Color(0xFFD96570))
    )
    val chatGptColor = Color(0xFF10A37F)
    
    val geminiBorderColor = Color(0xFF9B72CB)
    val chatGptBorderColor = Color(0xFF10A37F)
    
    val activeBorderColor = if (isGeminiActive) geminiBorderColor else chatGptBorderColor
    val activeInputFocusColor = if (isGeminiActive) Color(0xFF9B72CB) else Color(0xFF10A37F)

    Column(modifier = Modifier.fillMaxSize()) {
        
        // 1. Model Selection Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Engine Workspace",
                color = TextSecondary,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Gemini Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isGeminiActive) geminiGradient else Brush.linearGradient(listOf(DeepCharcoal, DeepCharcoal)))
                        .border(1.dp, if (isGeminiActive) Color.Transparent else BorderGlass, RoundedCornerShape(20.dp))
                        .clickable { onModelSelected("gemini") }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✨ Gemini",
                        color = if (isGeminiActive) ObsidianBlack else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // ChatGPT Chip
                val isChatGPTActive = selectedModel.lowercase() == "chatgpt"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isChatGPTActive) chatGptColor else DeepCharcoal)
                        .border(1.dp, if (isChatGPTActive) Color.Transparent else BorderGlass, RoundedCornerShape(8.dp))
                        .clickable { onModelSelected("chatgpt") }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "● ChatGPT",
                        color = if (isChatGPTActive) ObsidianBlack else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Active Model Profile Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(if (isGeminiActive) RoundedCornerShape(16.dp) else RoundedCornerShape(6.dp))
                .background(DeepCharcoal)
                .border(
                    width = 1.dp,
                    brush = if (isGeminiActive) {
                        Brush.horizontalGradient(listOf(Color(0xFF4285F4), Color(0xFFD96570)))
                    } else {
                        Brush.linearGradient(listOf(chatGptColor.copy(alpha = 0.6f), chatGptColor.copy(alpha = 0.2f)))
                    },
                    shape = if (isGeminiActive) RoundedCornerShape(16.dp) else RoundedCornerShape(6.dp)
                )
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isGeminiActive) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(colors = listOf(Color(0xFF4F46E5), Color(0xFFD946EF)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(
                            text = "Gemini Creative Workspace",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Dynamic brainstorming & expressive answers active.",
                            style = Typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(chatGptColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("●", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(
                            text = "ChatGPT Analytical Suite",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Structured reasoning & focus planning active.",
                            style = Typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Main Conversational Chat Log
        val chatLogBackground = if (isGeminiActive) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF130E26), // Cosmic deep violet
                    Color(0xFF0A0E12)  // Deep obsidian black
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    DeepCharcoal,
                    DeepCharcoal
                )
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(if (isGeminiActive) RoundedCornerShape(16.dp) else RoundedCornerShape(6.dp))
                .background(chatLogBackground)
                .border(
                    width = 1.dp,
                    color = activeBorderColor.copy(alpha = 0.3f),
                    shape = if (isGeminiActive) RoundedCornerShape(16.dp) else RoundedCornerShape(6.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message, isGeminiMode = isGeminiActive)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Quick Prompts suggestions (Interactive)
        val promptSuggestions = if (isGeminiActive) {
            listOf(
                "✨ Mindful haiku",
                "🔮 Creative meditation",
                "🧘 Guided visualization"
            )
        } else {
            listOf(
                "⚡ Optimize my tasks",
                "📝 Plan a focus routine",
                "🔍 Focus bottlenecks"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            promptSuggestions.forEach { prompt ->
                val promptShape = if (isGeminiActive) RoundedCornerShape(16.dp) else RoundedCornerShape(6.dp)
                val promptBorderColor = if (isGeminiActive) Color(0xFF9B72CB).copy(alpha = 0.5f) else Color(0xFF10A37F).copy(alpha = 0.5f)
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(promptShape)
                        .background(DeepCharcoal)
                        .border(1.dp, promptBorderColor, promptShape)
                        .clickable {
                            val query = when {
                                prompt.startsWith("✨ ") -> prompt.substring(2)
                                prompt.startsWith("🔮 ") -> prompt.substring(2)
                                prompt.startsWith("🧘 ") -> prompt.substring(2)
                                prompt.startsWith("⚡ ") -> prompt.substring(2)
                                prompt.startsWith("📝 ") -> prompt.substring(2)
                                prompt.startsWith("🔍 ") -> prompt.substring(2)
                                else -> prompt
                            }
                            onSendMessage(query)
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prompt,
                        style = Typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Chat Input Box Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val inputShape = if (isGeminiActive) RoundedCornerShape(24.dp) else RoundedCornerShape(6.dp)
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                placeholder = { Text("Ask Zen AI something...", color = TextTertiary) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = activeInputFocusColor,
                    unfocusedBorderColor = BorderGlass,
                    focusedContainerColor = ObsidianBlack,
                    unfocusedContainerColor = ObsidianBlack
                ),
                shape = inputShape
            )

            // Dynamic Action Button (Gradient for Gemini, Solid Teal for ChatGPT)
            val btnShape = if (isGeminiActive) RoundedCornerShape(24.dp) else RoundedCornerShape(6.dp)
            Box(
                modifier = Modifier
                    .clip(btnShape)
                    .background(if (isGeminiActive) geminiGradient else Brush.linearGradient(listOf(chatGptColor, chatGptColor)))
                    .clickable {
                        if (queryText.isNotBlank()) {
                            onSendMessage(queryText)
                            queryText = ""
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGeminiActive) "Spark" else "Send", 
                    color = ObsidianBlack, 
                    fontWeight = FontWeight.Bold,
                    style = Typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isGeminiMode: Boolean) {
    val isUser = message.isUser
    val text = message.text

    // Identify which brand generated this response
    val isGemini = text.startsWith("[Gemini")
    val isChatGPT = text.startsWith("[ChatGPT")

    // Clean prefix tags from the displayed text
    val cleanText = when {
        isGemini -> text.substringAfter("] ")
        isChatGPT -> text.substringAfter("] ")
        else -> text
    }

    val alignment = if (isUser) Alignment.End else Alignment.Start
    
    // Choose bubble shape based on model
    val shape = if (isUser) {
        if (isGeminiMode) {
            RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
        } else {
            RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
        }
    } else {
        if (isGemini || (isGeminiMode && !isChatGPT)) {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
        } else {
            RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Platform Label (Preceded by Brand Icons)
        if (!isUser) {
            val label = when {
                isGemini -> "✨ Gemini Assistant"
                isChatGPT -> "● ChatGPT Assistant"
                isGeminiMode -> "✨ Gemini Assistant"
                else -> "● ChatGPT Assistant"
            }
            val labelColor = when {
                isGemini || (isGeminiMode && !isChatGPT) -> Color(0xFF9B72CB)
                else -> Color(0xFF10A37F)
            }
            Text(
                text = label,
                color = labelColor,
                style = Typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            )
        }

        // Custom Bubble Background Styling
        val bubbleModifier = Modifier
            .clip(shape)
            .then(
                when {
                    isUser -> {
                        if (isGeminiMode) {
                            Modifier
                                .background(DeepCharcoal)
                                .border(1.dp, Color(0xFF9B72CB).copy(alpha = 0.5f), shape)
                        } else {
                            Modifier
                                .background(DeepCharcoal)
                                .border(1.dp, BorderGlass, shape)
                        }
                    }
                    isGemini || (isGeminiMode && !isChatGPT) -> Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF4F46E5), Color(0xFF9333EA)) // Google Indigo to Purple
                            )
                        )
                    else -> Modifier
                        .background(Color(0xFF10A37F)) // OpenAI Teal
                }
            )
            .padding(14.dp)

        Box(modifier = bubbleModifier) {
            Text(
                text = cleanText,
                color = TextPrimary,
                style = Typography.bodyMedium,
                lineHeight = 20.sp
            )
        }

        Text(
            text = message.timestamp,
            color = TextTertiary,
            style = Typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp, start = 6.dp, end = 6.dp)
        )
    }
}
