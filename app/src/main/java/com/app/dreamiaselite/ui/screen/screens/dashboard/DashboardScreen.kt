package com.app.dreamiaselite.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.dreamiaselite.ui.theme.*
import kotlinx.coroutines.delay

//-------------------------------------------------------
// Data Model
//-------------------------------------------------------

data class HomeSectionItem(
    val title: String,
    val subtitle: String,
    val tag: String,
    val meta: String
)

//-------------------------------------------------------
// MAIN DASHBOARD SCREEN
//-------------------------------------------------------

@Composable
fun DashboardScreen(navController: NavHostController, userName: String? = null) {

    // Remember data items to prevent recreation on each recomposition
    val caItems = remember {
        listOf(
            HomeSectionItem(
                "RBI Monetary Policy Review",
                "Economy • GS3",
                "Current Affairs",
                "Updated today"
            ),
            HomeSectionItem(
                "New Environment Treaty Signed",
                "Environment • GS3",
                "Current Affairs",
                "Key facts & analysis"
            ),
            HomeSectionItem(
                "Supreme Court ruling on FRs",
                "Polity • GS2",
                "Current Affairs",
                "Judgment highlights"
            )
        )
    }

    val testItems = remember {
        listOf(
            HomeSectionItem(
                "Prelims Full Test 1",
                "100 Q • Timed",
                "Test Series",
                "Calibrated to your level"
            ),
            HomeSectionItem(
                "Polity Sectional Test",
                "30 Q • FRs & DPSP",
                "Test Series",
                "Prev best: 72%"
            ),
            HomeSectionItem(
                "Economy Sectional Test",
                "35 Q • Inflation",
                "Test Series",
                "Adaptive difficulty"
            )
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
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item { DashboardHeroCard(userName) }

        item {
            SubjectChipRow(
                subjects = listOf("History", "Geography", "Polity", "Economy", "Science", "Environment"),
                onSubjectClick = { subject ->
                    val encoded = java.net.URLEncoder.encode(subject, "UTF-8")
                    navController.navigate("subject_dashboard/$encoded")
                }
            )
        }

        item {
            DashboardSection(
                title = "Monthly Current Affairs",
                caption = "Curated for UPSC CSE",
                pillColor = AccentCyan,
                items = caItems,
                highlightColor = AccentCyan
            )
        }

        item {
            DashboardSection(
                title = "Monthly Current Affairs Test",
                caption = "Based on your preparation pattern",
                pillColor = AccentLavender,
                items = testItems,
                highlightColor = AccentLavender
            )
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    caption: String,
    pillColor: Color,
    items: List<HomeSectionItem>,
    highlightColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = title,
            caption = caption,
            pillColor = pillColor,
            onViewAll = {}
        )

        HorizontalSectionList(
            items = items,
            highlightColor = highlightColor
        )
    }
}

//-------------------------------------------------------
// HERO CARD
//-------------------------------------------------------

@Composable
fun DashboardHeroCard(userName: String?) {
    val displayName = userName?.ifBlank { null } ?: "Aspirant"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            AccentPeach.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardBadge(text = "UPSC 2025", color = Gold)
                Text(
                    text = "112 days left",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                )
            }

            Text(
                text = "Welcome back, $displayName 👋",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            Text(
                text = "Dream IAS Elite",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            )

            Text(
                text = "Let's complete today’s targets and get closer to your rank.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            DailyProgressCard()
        }
    }
}

@Composable
fun DailyProgressCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Daily Test Progress",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = "0/3",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            )

            Text(
                text = "Keep pace to hit today’s target.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            LinearProgressIndicator(
                progress = 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = AccentCyan,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        }
    }
}

//-------------------------------------------------------
// SUBJECT CHIPS
//-------------------------------------------------------

@Composable
fun SubjectChipRow(
    subjects: List<String>,
    onSubjectClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(subjects, key = { it }) { subject ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .clickable { onSubjectClick(subject) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

//-------------------------------------------------------
// SECTION HEADER
//-------------------------------------------------------

@Composable
fun SectionHeader(
    title: String,
    caption: String,
    pillColor: Color,
    onViewAll: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(pillColor.copy(alpha = 0.20f))
                .clickable { onViewAll() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "View all",
                color = pillColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

//-------------------------------------------------------
// HORIZONTAL CAROUSEL (used by BOTH sections)
//-------------------------------------------------------

@Composable
fun HorizontalSectionList(items: List<HomeSectionItem>, highlightColor: Color) {

    val listState = rememberLazyListState()

    // Removed auto-scroll infinite loop that was causing performance issues
    // Users can now scroll manually without unwanted animations

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(top = 6.dp)
    ) {
        items(items) { item ->
            HomeCard(item, highlightColor)
        }
    }
}

//-------------------------------------------------------
// CARD UI (Same for CA + TEST SERIES)
//-------------------------------------------------------

@Composable
fun HomeCard(item: HomeSectionItem, highlightColor: Color) {

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(150.dp)
            .clickable { },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            highlightColor.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DashboardBadge(text = item.tag, color = highlightColor)
                    Text(
                        text = item.meta,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 2
                )

                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View details",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = highlightColor
                        )
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = highlightColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 22.dp, y = (-28).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                highlightColor.copy(alpha = 0.24f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun DashboardBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        )
    }
}
