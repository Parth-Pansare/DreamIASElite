package com.app.dreamiaselite.ui.screen.screens.studyplanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
// ---------- data model ----------

data class PlannerTask(
    val title: String,
    val subtitle: String,
    val isDone: Boolean = false
)

// ---------- main screen ----------

@Composable
fun StudyPlannerScreen() {
    val scrollState = rememberScrollState()

    var tasks by remember { mutableStateOf(listOf<PlannerTask>()) }

    val completedCount = tasks.count { it.isDone }
    val totalCount = tasks.size

    // dialogs and inline form state
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newSubtitle by remember { mutableStateOf("") }
    var showEditPicker by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editSubtitle by remember { mutableStateOf("") }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // header
        Text(
            text = "Study Planner",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Plan your day and track your weekly targets.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )

        PlannerSummaryCard(
            completedTasks = completedCount,
            totalTasks = totalCount
        )

        TodayPlanCard(
            tasks = tasks,
            onToggleTask = { index ->
                tasks = tasks.toMutableList().also { list ->
                    val old = list[index]
                    list[index] = old.copy(isDone = !old.isDone)
                }
            },
            onAddTask = { showAddDialog = true },
            onEditTask = {
                if (tasks.isNotEmpty()) {
                    showEditPicker = true
                }
            }
        )

        WeeklyTargetsCard()

        Spacer(Modifier.height(4.dp))

    }

    // ---------- Quick Add Dialog ----------

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add task to today’s plan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Task title") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubtitle,
                        onValueChange = { newSubtitle = it },
                        label = { Text("Details (subject • time)") },
                        singleLine = true,
                        placeholder = { Text("e.g. Polity • 45 min") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val subtitle = if (newSubtitle.isBlank())
                                "Custom task"
                            else
                                newSubtitle

                            tasks = tasks + PlannerTask(
                                title = newTitle.trim(),
                                subtitle = subtitle.trim()
                            )
                        }
                        // reset + dismiss
                        newTitle = ""
                        newSubtitle = ""
                        showAddDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        newTitle = ""
                        newSubtitle = ""
                        showAddDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------- Edit Task Picker ----------
    if (showEditPicker) {
        AlertDialog(
            onDismissRequest = { showEditPicker = false },
            title = { Text("Edit today’s task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (tasks.isEmpty()) {
                        Text("No tasks to edit yet.")
                    } else {
                        tasks.forEachIndexed { index, task ->
                            OutlinedButton(
                                onClick = {
                                    editingIndex = index
                                    editTitle = task.title
                                    editSubtitle = task.subtitle
                                    showEditPicker = false
                                    showEditDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(task.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        task.subtitle,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showEditPicker = false }
                ) { Text("Close") }
            }
        )
    }


    // ---------- Edit Task Dialog ----------
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editingIndex = null
            },
            title = { Text("Edit today’s task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Task title") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editSubtitle,
                        onValueChange = { editSubtitle = it },
                        label = { Text("Details") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val index = editingIndex
                        if (index != null && editTitle.isNotBlank()) {
                            tasks = tasks.toMutableList().also { list ->
                                val old = list[index]
                                list[index] = old.copy(
                                    title = editTitle.trim(),
                                    subtitle = editSubtitle.ifBlank { "Custom task" }.trim()
                                )
                            }
                        }
                        showEditDialog = false
                        editingIndex = null
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showEditDialog = false
                        editingIndex = null
                    }
                ) { Text("Cancel") }
            }
        )
    }
}

// ---------- summary card ----------

@Composable
private fun PlannerSummaryCard(
    completedTasks: Int,
    totalTasks: Int
) {
    val hoursTarget = 4
    val hoursDone = 2

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Today’s summary",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "Stay consistent. Small wins add up.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryStat(
                    label = "Study time",
                    value = "$hoursDone / $hoursTarget hrs",
                    modifier = Modifier.weight(1f)
                )
                SummaryStat(
                    label = "Tasks done",
                    value = "$completedTasks / $totalTasks",
                    modifier = Modifier.weight(1f)
                )
                SummaryStat(
                    label = "Streak",
                    value = "4 days 🔥",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
    }
}

// ---------- today tasks card ----------

@Composable
private fun TodayPlanCard(
    tasks: List<PlannerTask>,
    onToggleTask: (Int) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Today’s plan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Mark tasks as done as you progress.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = onAddTask,
                        contentPadding = ButtonDefaults.ContentPadding
                    ) { Text("+ New") }
                    OutlinedButton(
                        onClick = onEditTask,
                        enabled = tasks.isNotEmpty(),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) { Text("Edit") }
                }
            }

            Spacer(Modifier.height(8.dp))

            tasks.forEachIndexed { index, task ->
                PlannerTaskRow(
                    task = task,
                    onToggle = { onToggleTask(index) }
                )
            }
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks added yet. Tap “+ New” to plan your day.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                )
            }
        }
    }
}

@Composable
private fun PlannerTaskRow(
    task: PlannerTask,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() }
            )
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.isDone) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        },
                       color = if (task.isDone) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                )
                Text(
                    text = task.subtitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

// ---------- weekly targets ----------

@Composable
private fun WeeklyTargetsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Weekly targets",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "Keep at least 70% of your weekly plan consistent.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            Spacer(Modifier.height(8.dp))

            val days = listOf("M", "T", "W", "T", "F", "S", "S")
            val completion = listOf(0.3f, 0.7f, 0.4f, 0.8f, 0.6f, 0.2f, 0.0f)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                days.forEachIndexed { index, day ->
                    DayCompletionCircle(
                        label = day,
                        progress = completion[index]
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCompletionCircle(
    label: String,
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
    }
}
