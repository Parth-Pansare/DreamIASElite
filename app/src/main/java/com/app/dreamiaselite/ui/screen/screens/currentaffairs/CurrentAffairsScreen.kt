package com.app.dreamiaselite.ui.screens.currentaffairs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.composed
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// ---------------- DATA MODELS ----------------

data class CurrentAffairArticle(
    val id: Int,
    val category: String,       // e.g. "International", "Economy"
    val importance: String,     // e.g. "High", "Moderate"
    val title: String,
    val summary: String,
    val date: String,           // e.g. "Nov 20"
    val readTime: String,       // e.g. "8 min read"
    val tag: String             // broad tag used for chips (Economy, Polity, etc.)
)

// ---------------- MAIN SCREEN ----------------

@Composable
fun CurrentAffairsScreen() {
    val listState = rememberLazyListState()

    // UPSC-relevant chips
    val chips = listOf(
        "All",
        "National",
        "International",
        "Polity & Governance",
        "Economy",
        "Environment & Ecology",
        "Science & Tech",
        "International Relations",
        "Security",
        "Social Issues",
        "Schemes & Policies",
        "Reports & Indices",
        "Places in News",
        "Bills & Acts",
        "Miscellaneous"
    )

    // Fake article data for now
    val allArticles = remember {
        listOf(
            CurrentAffairArticle(
                id = 1,
                category = "International",
                importance = "High",
                title = "G20 Summit 2024: Key Outcomes and India's Role",
                summary = "Major announcements on climate change, global economic cooperation and India's growing diplomatic influence.",
                date = "Nov 20",
                readTime = "8 min read",
                tag = "International"
            ),
            CurrentAffairArticle(
                id = 2,
                category = "Economy",
                importance = "High",
                title = "New Economic Survey Released: GDP Growth Projections",
                summary = "The latest Economic Survey projects India's GDP growth at 6.5% for the upcoming fiscal year with focus on infrastructure and manufacturing.",
                date = "Nov 19",
                readTime = "12 min read",
                tag = "Economy"
            ),
            CurrentAffairArticle(
                id = 3,
                category = "Polity & Governance",
                importance = "Moderate",
                title = "Key Amendments Proposed to the RTI Act",
                summary = "The proposed amendments aim to modify the tenure and service conditions of Information Commissioners.",
                date = "Nov 18",
                readTime = "6 min read",
                tag = "Polity & Governance"
            ),
            CurrentAffairArticle(
                id = 4,
                category = "Environment & Ecology",
                importance = "High",
                title = "New Wetland Conservation Rules Notified",
                summary = "The government has notified updated rules for identification and conservation of wetlands across the country.",
                date = "Nov 18",
                readTime = "7 min read",
                tag = "Environment & Ecology"
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }
    var bookmarkedIds by remember { mutableStateOf(setOf<Int>()) }

    // Filter logic: by chip + search (optimized with derivedStateOf to avoid unnecessary recalculations)
    val filteredArticles by remember {
        androidx.compose.runtime.derivedStateOf {
            allArticles.filter { article ->
                val matchesChip =
                    selectedChip == "All" || article.tag == selectedChip || article.category == selectedChip
                val matchesSearch = searchQuery.isBlank() ||
                        article.title.contains(searchQuery, ignoreCase = true) ||
                        article.summary.contains(searchQuery, ignoreCase = true)
                matchesChip && matchesSearch
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 80.dp
            )
        ) {
            item {
                ScreenHeader()
                Spacer(Modifier.height(12.dp))
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
                Spacer(Modifier.height(12.dp))
                CurrentAffairsChipsRow(
                    chips = chips,
                    selectedChip = selectedChip,
                    onChipSelected = { selectedChip = it }
                )
                Spacer(Modifier.height(12.dp))
                WeeklyHighlightCard()
                Spacer(Modifier.height(8.dp))
            }

            items(filteredArticles, key = { it.id }) { article ->
                CurrentAffairArticleCard(
                    article = article,
                    isBookmarked = bookmarkedIds.contains(article.id),
                    onBookmarkClick = {
                        bookmarkedIds = if (bookmarkedIds.contains(article.id)) {
                            bookmarkedIds - article.id
                        } else {
                            bookmarkedIds + article.id
                        }
                    }
                )
            }
        }
    }
}

// ---------------- SMALL COMPOSABLES ----------------

@Composable
private fun ScreenHeader() {
    Text(
        text = "Current Affairs",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(top = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search current affairs...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun CurrentAffairsChipsRow(
    chips: List<String>,
    selectedChip: String,
    onChipSelected: (String) -> Unit
) {
    // simple wrapping row – you could change to LazyRow if you want strictly horizontal scroll
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            val selected = chip == selectedChip
            FilterChip(
                selected = selected,
                onClick = { onChipSelected(chip) },
                label = {
                    Text(
                        text = chip,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun WeeklyHighlightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF305CFF) // keep a bold highlight card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = "42 New Updates",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Stay updated with daily current affairs.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.85f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = "Open weekly CA",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun CurrentAffairArticleCard(
    article: CurrentAffairArticle,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top row with badges + bookmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgeChip(
                        text = article.category,
                        background = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        foreground = MaterialTheme.colorScheme.primary
                    )
                    BadgeChip(
                        text = article.importance,
                        background = Color(0xFFFFF0F0),
                        foreground = Color(0xFFE53935)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isBookmarked)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            else
                                Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBookmarked)
                            Icons.Outlined.Bookmark
                        else
                            Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(2.dp)
                            .clickableWithoutRipple(onBookmarkClick)
                    )
                }
            }

            // Title
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            // Summary
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Date + read time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = article.date,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = article.readTime,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { /* TODO: open detail screen or webview */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Read Full Article",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

// simple pill badge
@Composable
private fun BadgeChip(
    text: String,
    background: Color,
    foreground: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = foreground,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

// helper to avoid ripple on the tiny bookmark icon
// Note: MutableInteractionSource is now properly remembered to avoid recreation on every recomposition
@Composable
private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}
