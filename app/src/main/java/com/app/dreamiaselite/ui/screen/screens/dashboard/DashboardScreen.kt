package com.app.dreamiaselite.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.dreamiaselite.ui.theme.AccentCyan
import com.app.dreamiaselite.ui.theme.AccentLavender
import com.app.dreamiaselite.ui.theme.AccentPeach
import com.app.dreamiaselite.ui.theme.Gold
import com.app.dreamiaselite.ui.theme.LightBackground
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextSecondary

data class HomeSectionItem(
    val title: String,
    val subtitle: String
)

@Composable
fun DashboardScreen(navController: NavHostController) {
    val caItems = listOf(
        HomeSectionItem("RBI Monetary Policy Review", "Economy • GS3"),
        HomeSectionItem("New Environment Treaty Signed", "Environment • GS3"),
        HomeSectionItem("Supreme Court ruling on FRs", "Polity • GS2")
    )

    val testItems = listOf(
        HomeSectionItem("Prelims Full Test 1", "100 Q • Timed"),
        HomeSectionItem("Polity Sectional Test", "30 Q • FRs & DPSP"),
        HomeSectionItem("Economy Sectional Test", "35 Q • Inflation")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderHeroCard()

        SubjectChipsRow()

        SectionWithCarousel(
            title = "Today’s Current Affairs",
            caption = "Curated for UPSC CSE",
            items = caItems,
            highlightColor = AccentCyan
        )

        SectionWithCarousel(
            title = "Test Series For You",
            caption = "Based on your preparation pattern",
            items = testItems,
            highlightColor = AccentLavender
        )
    }
}

@Composable
private fun HeaderHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightSurface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()                    // 🟢 IMPORTANT LINE
                .background(
                    Brush.linearGradient(
                        listOf(
                            AccentPeach.copy(alpha = 0.3f),
                            AccentCyan.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome back, Aspirant 👋",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary
                    )
                )
                Text(
                    text = "Dream IAS Elite",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                )
                Text(
                    text = "Let’s complete today’s targets and get closer to your rank.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary
                    )
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = "Study Time",
                        value = "3 hrs",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "MCQs Solved",
                        value = "40/80",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Streak",
                        value = "5 days",
                        emoji = "🔥",
                        modifier = Modifier.weight(1f)
                    )
                }


            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    emoji: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightSurface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                )
                if (emoji.isNotEmpty()) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}


@Composable
private fun SubjectChipsRow() {
    val subjects = listOf("Polity", "Economy", "History", "Geography", "Environment", "Sci & Tech")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(subjects) { subject ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LightSurface)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }

    Spacer(Modifier.height(4.dp))
}

@Composable
private fun SectionWithCarousel(
    title: String,
    caption: String,
    items: List<HomeSectionItem>,
    highlightColor: androidx.compose.ui.graphics.Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary
                    )
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(highlightColor.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = highlightColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                HomeItemCard(item)
            }
        }
    }
}

@Composable
private fun HomeItemCard(item: HomeSectionItem) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .height(120.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )
        }
    }
}
