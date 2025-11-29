package com.app.dreamiaselite.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.dreamiaselite.ui.theme.AccentCyan
import com.app.dreamiaselite.ui.theme.AccentLavender
import com.app.dreamiaselite.ui.theme.AccentPeach
import com.app.dreamiaselite.ui.theme.Gold

data class SubjectCardItem(val title: String, val subtitle: String, val meta: String)
data class TopicProgress(val title: String, val progress: String)

@Composable
fun SubjectDashboardScreen(subject: String, navController: NavHostController) {

    val dailyMcqs = remember(subject) {
        listOf(
            SubjectCardItem("5 Quick MCQs", "Mixed difficulty", "Auto-filtered to $subject"),
            SubjectCardItem("Timed drill • 8 Qs", "4 mins", "Great for warm-up")
        )
    }

    val topics = remember(subject) {
        listOf(
            TopicProgress("$subject Basics", "70%"),
            TopicProgress("Important Acts", "40%"),
            TopicProgress("Previous Year Themes", "55%")
        )
    }

    val caArticles = remember(subject) {
        listOf(
            SubjectCardItem("Key $subject CA digest", "3 articles", "Updated today"),
            SubjectCardItem("Exam-oriented briefs", "2 mins each", "Flag for revision")
        )
    }

    val tests = remember(subject) {
        listOf(
            SubjectCardItem("$subject sectional test", "30 Q • Adaptive", "Prev best: 68%"),
            SubjectCardItem("Mini drill", "10 Q • Timed", "Good for quick check")
        )
    }

    val notes = remember(subject) {
        listOf(
            SubjectCardItem("Last opened note", "Annotations saved", "Resume"),
            SubjectCardItem("Flashcards", "Key terms & dates", "Review 5 cards")
        )
    }

    val pyqs = remember(subject) {
        listOf(
            SubjectCardItem("$subject PYQ set", "10 Q • 2023-2021", "Avg accuracy 62%"),
            SubjectCardItem("High-yield PYQs", "Curated mix", "Revise with notes")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.popBackStack() }
                        .padding(6.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Column {
                    Text(
                        text = "$subject Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Everything auto-filtered to $subject",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }

        item { AccuracyCard() }

        item {
            SubjectSection(
                title = "Daily MCQs",
                caption = "Short drills to stay sharp",
                pillColor = AccentCyan,
                items = dailyMcqs
            )
        }

        item {
            TopicListSection(
                title = "Topics",
                caption = "Focus areas for $subject",
                pillColor = AccentPeach,
                topics = topics
            )
        }

        item {
            SubjectSection(
                title = "Important CA articles",
                caption = "$subject-linked current affairs",
                pillColor = AccentLavender,
                items = caArticles
            )
        }

        item {
            SubjectSection(
                title = "Tests in $subject",
                caption = "Sectionals and drills",
                pillColor = Gold,
                items = tests
            )
        }

        item {
            SubjectSection(
                title = "Notes",
                caption = "Your saved material",
                pillColor = AccentPeach,
                items = notes
            )
        }

        item {
            SubjectSection(
                title = "PYQs",
                caption = "Filtered for $subject",
                pillColor = AccentCyan,
                items = pyqs
            )
        }

        item { Spacer(Modifier.height(6.dp)) }
    }
}

@Composable
private fun SubjectSection(
    title: String,
    caption: String,
    pillColor: Color,
    items: List<SubjectCardItem>
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = title, caption = caption, pillColor = pillColor) {}

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                SubjectCard(item, pillColor)
            }
        }
    }
}

@Composable
private fun TopicListSection(
    title: String,
    caption: String,
    pillColor: Color,
    topics: List<TopicProgress>
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = title, caption = caption, pillColor = pillColor) {}
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            topics.forEach { topic ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Progress ${topic.progress}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            )
                        }
                        Text(
                            text = topic.progress,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = pillColor
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(item: SubjectCardItem, highlightColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            highlightColor.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Text(
                text = item.meta,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = highlightColor,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun AccuracyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Accuracy & Progress",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Accuracy 68% • Attempts 120 • Streak 3 days",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            )
            Text(
                text = "Weak spots: Important Acts, Modern basics",
                style = MaterialTheme.typography.bodySmall.copy(color = AccentPeach)
            )
        }
    }
}
