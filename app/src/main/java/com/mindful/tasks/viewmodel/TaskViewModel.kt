package com.mindful.tasks.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class TaskViewModel : ViewModel() {

    // Internal mutable state flows
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    private val _currentJournalPrompt = MutableStateFlow("What made you smile today?")

    // Public read-only state flows
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()
    val currentJournalPrompt: StateFlow<String> = _currentJournalPrompt.asStateFlow()

    init {
        // Add sample data for a beautiful initial layout state
        _tasks.value = listOf(
            Task(title = "Morning Meditation", isCompleted = true, priority = Priority.HIGH, category = "Mind"),
            Task(title = "Drink 3L of Water", isCompleted = false, priority = Priority.MEDIUM, category = "Health"),
            Task(title = "Code 1 hour in Android Studio", isCompleted = false, priority = Priority.HIGH, category = "Skill"),
            Task(title = "Read 10 pages of a book", isCompleted = false, priority = Priority.LOW, category = "Mind")
        )

        _journalEntries.value = listOf(
            JournalEntry(
                text = "Felt super excited today about starting my first Android coding journey. Git set up was a breeze!",
                date = getFormattedDate(),
                moodEmoji = "🚀"
            )
        )
    }

    // Task actions
    fun addTask(title: String, priority: Priority, category: String) {
        if (title.isBlank()) return
        val newTask = Task(title = title, priority = priority, category = category)
        _tasks.value = _tasks.value + newTask
    }

    fun toggleTask(taskId: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(isCompleted = !task.isCompleted)
            } else {
                task
            }
        }
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }

    // Journal actions
    fun addJournalEntry(text: String, moodEmoji: String) {
        if (text.isBlank()) return
        val newEntry = JournalEntry(
            text = text,
            date = getFormattedDate(),
            moodEmoji = moodEmoji
        )
        _journalEntries.value = listOf(newEntry) + _journalEntries.value
        rotateJournalPrompt()
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
}
