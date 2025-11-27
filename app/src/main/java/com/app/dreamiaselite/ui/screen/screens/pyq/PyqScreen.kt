package com.app.dreamiaselite.ui.screen.screens.pyq

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.composed
import com.app.dreamiaselite.ui.theme.LightBackground
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextPrimary
import com.app.dreamiaselite.ui.theme.TextSecondary

// Needed for Modifier.weight()
//import androidx.compose.foundation.layout.weight



private data class PyqPaper(
    val title: String,
    val questions: String,
    val statusLabel: String? = null,
    val statusColor: Color = Color(0xFF2ECF7C),
    val score: Int? = null,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
    val primaryAction: PaperAction,
    val secondaryAction: PaperAction
)

private data class PaperAction(
    val label: String,
    val style: ActionStyle,
    val leadingIcon: ImageVector? = null
)

private enum class ActionStyle { Primary, Outline, Tonal }


@Composable
fun PyqScreen() {

    val tabs = listOf("Year-wise", "Subject-wise")
    val selectedTab = remember { mutableStateOf(tabs.first()) }
    val years = listOf("2024", "2023", "2022", "2021", "2020")
    val selectedYear = remember { mutableStateOf("2023") }
    val subjects = listOf("GS Paper 1", "GS Paper 2", "GS Paper 3", "CSAT")
    val selectedSubject = remember { mutableStateOf(subjects.first()) }

    val papers = listOf(
        PyqPaper(
            title = "GS Paper 1",
            questions = "100 Questions",
            statusLabel = "Downloaded",
            score = 68,
            icon = Icons.Outlined.CheckCircle,
            iconTint = Color(0xFF28B463),
            iconBackground = Color(0xFFE8F7EF),
            primaryAction = PaperAction("Reattempt", ActionStyle.Primary),
            secondaryAction = PaperAction("View Solutions", ActionStyle.Outline)
        ),
        PyqPaper(
            title = "GS Paper 2",
            questions = "100 Questions",
            statusLabel = "Downloaded",
            score = null,
            icon = Icons.Outlined.Quiz,
            iconTint = Color(0xFF2E86DE),
            iconBackground = Color(0xFFE8F1FF),
            primaryAction = PaperAction("Start Solving", ActionStyle.Primary),
            secondaryAction = PaperAction("Download", ActionStyle.Tonal, Icons.Outlined.Download)
        ),
        PyqPaper(
            title = "GS Paper 3",
            questions = "100 Questions",
            statusLabel = null,
            score = null,
            icon = Icons.Outlined.Assignment,
            iconTint = Color(0xFF8E44AD),
            iconBackground = Color(0xFFF3E9FF),
            primaryAction = PaperAction("Start Solving", ActionStyle.Primary),
            secondaryAction = PaperAction("Download", ActionStyle.Tonal, Icons.Outlined.Download)
        ),
        PyqPaper(
            title = "CSAT",
            questions = "80 Questions",
            statusLabel = "Downloaded",
            score = 72,
            icon = Icons.Outlined.WorkspacePremium,
            iconTint = Color(0xFF16A085),
            iconBackground = Color(0xFFE6FAF3),
            primaryAction = PaperAction("Reattempt", ActionStyle.Primary),
            secondaryAction = PaperAction("View Solutions", ActionStyle.Outline)
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Text(
                text = "Previous Year Questions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
        }

        item {
            FilterTabs(
                tabs = tabs,
                selectedTab = selectedTab.value,
                onTabSelected = { selectedTab.value = it }
            )
        }

        item {
            if (selectedTab.value == "Year-wise") {
                YearSelector(years, selectedYear.value) { selectedYear.value = it }
            } else {
                SubjectSelector(subjects, selectedSubject.value) { selectedSubject.value = it }
            }
        }

        item { AttemptSummaryCard() }

        item {
            Text(
                text = if (selectedTab.value == "Year-wise") "${selectedYear.value} Papers" else "${selectedSubject.value} Papers",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
        }

        items(papers) { paper ->
            PyqPaperCard(paper)
        }

        item { StatsRow() }

        item { StudyTipCard() }
    }
}


@Composable
private fun FilterTabs(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF2E6BFF) else Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .clickableWithoutRipple { onTabSelected(tab) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearSelector(years: List<String>, selectedYear: String, onSelect: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        years.forEach { year ->
            val isSelected = year == selectedYear

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFE8ECFF) else LightSurface,
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE3E6EC)) else null
            ) {
                Box(
                    modifier = Modifier
                        .clickableWithoutRipple { onSelect(year) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = year,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF2E6BFF) else TextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectSelector(subjects: List<String>, selectedSubject: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        subjects.forEach { subject ->
            val isSelected = subject == selectedSubject
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFE8ECFF) else LightSurface,
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE3E6EC)) else null
            ) {
                Box(
                    modifier = Modifier
                        .clickableWithoutRipple { onSelect(subject) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF2E6BFF) else TextPrimary
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun AttemptSummaryCard() {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF9C1BEA), Color(0xFF6C1BCE))
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = "Papers Attempted",
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "5 / 15",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Outlined.CloudDownload,
                                contentDescription = "download",
                                tint = Color.White
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = 5f / 15f,
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Text(
                    text = "Keep solving! PYQs are the key to success 🎯",
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}


@Composable
private fun PyqPaperCard(paper: PyqPaper) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(paper.iconBackground),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = paper.icon,
                            contentDescription = paper.title,
                            tint = paper.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = paper.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = paper.questions,
                            color = TextSecondary
                        )
                    }
                }

                paper.statusLabel?.let {
                    StatusBadge(label = it, color = paper.statusColor)
                }
            }

            paper.score?.let { score ->
                ScoreStrip(score)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {

                ActionButton(
                    action = paper.secondaryAction,
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    action = paper.primaryAction,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun StatusBadge(label: String, color: Color) {

    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}


@Composable
private fun ScoreStrip(score: Int) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0F9F0)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Your Score", color = TextSecondary)
            Text("$score%", color = Color(0xFF17B26A), fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
private fun ActionButton(action: PaperAction, modifier: Modifier = Modifier) {

    when (action.style) {

        ActionStyle.Primary -> {
            Button(
                modifier = modifier,
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White
                )
            ) {
                Text(action.label)
            }
        }

        ActionStyle.Outline -> {
            OutlinedButton(
                modifier = modifier,
                onClick = { }
            ) {
                Text(action.label, color = TextPrimary)
            }
        }

        ActionStyle.Tonal -> {
            FilledTonalButton(
                modifier = modifier,
                onClick = { },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFFF3F5F8),
                    contentColor = TextPrimary
                )
            ) {

                action.leadingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(action.label)
            }
        }
    }
}


@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            label = "Latest Year Papers",
            value = "5",
            icon = Icons.Outlined.Book,
            tint = Color(0xFF2E6BFF),
            bubble = Color(0xFFE8EDFF),
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            label = "Completed Papers",
            value = "5",
            icon = Icons.Outlined.PlayCircle,
            tint = Color(0xFF1BA97F),
            bubble = Color(0xFFE8F6F1),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    bubble: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(bubble),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        }
    }
}


@Composable
private fun StudyTipCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6F1E)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Study Tip",
                tint = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                Text(
                    text = "Study Tip",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = "Analyze the pattern of questions asked in previous years. Focus on frequently asked topics and practice at least 2–3 papers from each subject.",
                    color = Color.White
                )
            }
        }
    }
}


private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
