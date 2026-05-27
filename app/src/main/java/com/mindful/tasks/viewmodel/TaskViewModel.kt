package com.mindful.tasks.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mindful.tasks.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class Priority { LOW, MEDIUM, HIGH }

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "Life"
)

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val date: String,
    val moodEmoji: String
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("mindful_prefs", Context.MODE_PRIVATE)

    // User authentication states
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    // Internal mutable state flows
    private val _geminiMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _chatGptMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _selectedAiModel = MutableStateFlow("gemini")

    // Public read-only state flows
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()
    val currentJournalPrompt: StateFlow<String> = _currentJournalPrompt.asStateFlow()
    val geminiMessages: StateFlow<List<ChatMessage>> = _geminiMessages.asStateFlow()
    val chatGptMessages: StateFlow<List<ChatMessage>> = _chatGptMessages.asStateFlow()
    val selectedAiModel: StateFlow<String> = _selectedAiModel.asStateFlow()

    init {
        checkSavedUser()
    }

    // Checks if the user was previously logged in on this phone
    private fun checkSavedUser() {
        val savedUsername = sharedPrefs.getString("username", null)
        if (savedUsername != null) {
            _currentUser.value = savedUsername
            fetchUserData(savedUsername)
        }
    }

    // Handles user session start
    fun loginSession(username: String) {
        sharedPrefs.edit().putString("username", username).apply()
        _currentUser.value = username
        
        // Reset chat rooms
        _geminiMessages.value = listOf(
            ChatMessage(text = "[Gemini] Hello! I am your Gemini Creative Assistant. How can I spark your mindfulness or design a relaxing experience today?", isUser = false)
        )
        _chatGptMessages.value = listOf(
            ChatMessage(text = "[ChatGPT] Hello! I am your ChatGPT Analytical Assistant. Let's optimize your productivity and schedule. What focus goal are we targeting?", isUser = false)
        )
        
        fetchUserData(username)
    }

    // Fetches profile data from REST API
    private fun fetchUserData(username: String) {
        viewModelScope.launch {
            val serverTasks = NetworkClient.getTasks(username)
            if (serverTasks.isNotEmpty()) {
                _tasks.value = serverTasks
            } else {
                // Pre-populate with beautiful initial tasks for new users
                _tasks.value = listOf(
                    Task(title = "Morning Meditation", isCompleted = true, priority = Priority.HIGH, category = "Mind"),
                    Task(title = "Drink 3L of Water", isCompleted = false, priority = Priority.MEDIUM, category = "Health"),
                    Task(title = "Add a new task in MindfulTasks", isCompleted = false, priority = Priority.HIGH, category = "Skill")
                )
                NetworkClient.saveTasks(username, _tasks.value)
            }

            val serverJournal = NetworkClient.getJournal(username)
            if (serverJournal.isNotEmpty()) {
                _journalEntries.value = serverJournal
            } else {
                _journalEntries.value = listOf(
                    JournalEntry(
                        text = "Signed in successfully and ready to start logging reflections!",
                        date = getFormattedDate(),
                        moodEmoji = "🚀"
                    )
                )
                NetworkClient.saveJournal(username, _journalEntries.value)
            }
        }
    }

    // User logout
    fun logout() {
        sharedPrefs.edit().remove("username").apply()
        _currentUser.value = null
        _tasks.value = emptyList()
        _journalEntries.value = emptyList()
        _geminiMessages.value = emptyList()
        _chatGptMessages.value = emptyList()
    }

    // Task actions
    fun addTask(title: String, priority: Priority, category: String) {
        val username = _currentUser.value ?: return
        if (title.isBlank()) return
        val newTask = Task(title = title, priority = priority, category = category)
        val updated = _tasks.value + newTask
        _tasks.value = updated
        
        viewModelScope.launch {
            NetworkClient.saveTasks(username, updated)
        }
    }

    fun toggleTask(taskId: String) {
        val username = _currentUser.value ?: return
        val updated = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else {
                task
            }
        }
        _tasks.value = updated

        viewModelScope.launch {
            NetworkClient.saveTasks(username, updated)
        }
    }

    fun deleteTask(taskId: String) {
        val username = _currentUser.value ?: return
        val updated = _tasks.value.filter { it.id != taskId }
        _tasks.value = updated

        viewModelScope.launch {
            NetworkClient.saveTasks(username, updated)
        }
    }

    // Journal actions
    fun addJournalEntry(text: String, moodEmoji: String) {
        val username = _currentUser.value ?: return
        if (text.isBlank()) return
        val newEntry = JournalEntry(
            text = text,
            date = getFormattedDate(),
            moodEmoji = moodEmoji
        )
        val updated = listOf(newEntry) + _journalEntries.value
        _journalEntries.value = updated
        rotateJournalPrompt()

        viewModelScope.launch {
            NetworkClient.saveJournal(username, updated)
        }
    }

    private fun rotateJournalPrompt() {
        val prompts = listOf(
            "What made you smile today?",
            "What is one thing you are grateful for?",
            "What was the most peaceful moment of your day?",
            "How did you overcome a challenge today?"
        )
        val currentIndex = prompts.indexOf(_currentJournalPrompt.value)
        val nextIndex = (currentIndex + 1) % prompts.size
        _currentJournalPrompt.value = prompts[nextIndex]
    }

    private fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
        return formatter.format(Date())
    }

    // ----------------------------------------------------
    // AI CHAT METHODS
    // ----------------------------------------------------

    fun setAiModel(model: String) {
        _selectedAiModel.value = model
    }

    fun sendAiQuery(text: String) {
        if (text.isBlank()) return
        val currentModel = _selectedAiModel.value.lowercase()
        val isGemini = currentModel == "gemini"
        
        // Append user query message to corresponding stream
        val userMsg = ChatMessage(text = text, isUser = true)
        if (isGemini) {
            _geminiMessages.value = _geminiMessages.value + userMsg
        } else {
            _chatGptMessages.value = _chatGptMessages.value + userMsg
        }

        viewModelScope.launch {
            // Append a temporary loading bubble to active stream
            val loadingText = if (isGemini) "[Gemini] Writing response..." else "[ChatGPT] Writing response..."
            val loadingMsg = ChatMessage(text = loadingText, isUser = false)
            if (isGemini) {
                _geminiMessages.value = _geminiMessages.value + loadingMsg
            } else {
                _chatGptMessages.value = _chatGptMessages.value + loadingMsg
            }

            val aiResponse = NetworkClient.queryAi(text, currentModel)

            // Replace loading bubble with the actual response in active stream
            if (isGemini) {
                _geminiMessages.value = _geminiMessages.value.filter { it.text != loadingText } + ChatMessage(text = aiResponse, isUser = false)
            } else {
                _chatGptMessages.value = _chatGptMessages.value.filter { it.text != loadingText } + ChatMessage(text = aiResponse, isUser = false)
            }
        }
    }
}
