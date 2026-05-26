package com.mindful.tasks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.mindful.tasks.viewmodel.JournalEntry
import com.mindful.tasks.viewmodel.Priority
import com.mindful.tasks.viewmodel.Task
import com.mindful.tasks.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val journalPrompt by viewModel.currentJournalPrompt.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Tasks, 1 = Journal

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Composable
            MindfulHeader()

            // Custom Glass-style Tab Selector
            TabSelector(activeTab = activeTab, onTabSelected = { activeTab = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content Area based on Selected Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                if (activeTab == 0) {
                    TasksTab(
                        tasks = tasks,
                        onAddTask = { title, priority, cat -> viewModel.addTask(title, priority, cat) },
                        onToggleTask = { viewModel.toggleTask(it) },
                        onDeleteTask = { viewModel.deleteTask(it) }
                    )
                } else {
                    JournalTab(
                        entries = journalEntries,
                        prompt = journalPrompt,
                        onAddEntry = { text, mood -> viewModel.addJournalEntry(text, mood) }
                    )
                }
            }
        }
    }
}

@Composable
fun MindfulHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "MindfulTasks",
            style = Typography.displayLarge,
            color = EmeraldZen,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Breathe in. Plan. Create your zen space.",
            style = Typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
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
        val tabs = listOf("Tasks Planner", "Zen Journal")
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
                        unfocusedBorderColor = BorderGlass
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
                        unfocusedBorderColor = BorderGlass
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
                    containerColor = ObsidianBlack
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
