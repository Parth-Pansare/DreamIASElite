@file:OptIn(ExperimentalLayoutApi::class)
package com.app.dreamiaselite.ui.screen.screens.notes

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
// ---------- data models ----------

data class NoteFolder(
    val name: String,
    val count: Int,
    val color: Color,
    val icon: ImageVector,
    val iconTint: Color
)

data class NoteItem(
    val id: Int,
    val subjectTag: String, // e.g. "Polity", "CA"
    val date: String,       // "Nov 16"
    val title: String,
    val preview: String,
    val tags: List<String>
)

data class ReferenceBook(
    val title: String,
    val author: String,
    val summary: String,
    val units: List<String>
)

// ---------- main screen ----------

@Composable
fun NotesScreen(navController: NavController) {
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }

    // mock folders
    val folders = remember {
        listOf(
            NoteFolder("History", 24, Color(0xFFEBF2FF), Icons.Outlined.MenuBook, Color(0xFF3B6EFF)),
            NoteFolder("Polity", 18, Color(0xFFF3E7FF), Icons.Outlined.AccountBalance, Color(0xFF9C27B0)),
            NoteFolder("Geography", 31, Color(0xFFE7F7EE), Icons.Outlined.Public, Color(0xFF2E7D32)),
            NoteFolder("Economy", 15, Color(0xFFFFF0E2), Icons.Outlined.Savings, Color(0xFFEF6C00)),
            NoteFolder("Science & Technology", 14, Color(0xFFF4EDFF), Icons.Outlined.Science, Color(0xFF512DA8)),
            NoteFolder("Environment & Ecology", 12, Color(0xFFEAF7F0), Icons.Outlined.Eco, Color(0xFF1B8A4B))
        )
    }

    // mock notes
    val notes = remember {
        listOf(
            NoteItem(
                id = 1,
                subjectTag = "Polity",
                date = "Nov 20",
                title = "Indian Constitution - Fundamental Rights",
                preview = "Articles 12–35 cover fundamental rights including Right to Equality, Right to Freedom...",
                tags = listOf("Fundamental Rights", "GS2", "Mains")
            ),
            NoteItem(
                id = 2,
                subjectTag = "CA",
                date = "Nov 16",
                title = "Current Affairs - G20 Summit",
                preview = "Key outcomes from G20 Summit 2024. India's presidency highlights and achievements...",
                tags = listOf("International", "G20", "Current")
            )
        )
    }

    val filteredNotes = notes.filter { note ->
        searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.preview.contains(searchQuery, ignoreCase = true) ||
                note.tags.any { it.contains(searchQuery, ignoreCase = true) }
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
                NotesHeader()
                Spacer(Modifier.height(16.dp))

                TotalNotesCard(
                    totalNotes = notes.size,
                    folderCount = folders.size
                )

                Spacer(Modifier.height(16.dp))

                FoldersSection(folders) { folderName ->
                    val encoded = Uri.encode(folderName)
                    navController.navigate("notes_reference/$encoded")
                }

                Spacer(Modifier.height(16.dp))

                RecentNotesHeader(
                    onViewAllClick = {
                        // TODO: navigate to all notes list
                    }
                )

                Spacer(Modifier.height(8.dp))
            }

            items(filteredNotes, key = { it.id }) { note ->
                NoteCard(
                    note = note,
                    onEditClick = { /* TODO */ },
                    onShareClick = { /* TODO */ },
                    onDeleteClick = { /* TODO */ }
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                ImportShareRow(
                    onImportClick = { /* TODO */ },
                    onShareAllClick = { /* TODO */ }
                )
                Spacer(Modifier.height(16.dp))
                NoteTakingTipCard()
            }
        }
    }
}

// ---------- header & search ----------

@Composable
private fun NotesHeader(
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notes",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

// ---------- total notes + folders ----------

@Composable
private fun TotalNotesCard(
    totalNotes: Int,
    folderCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF305CFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Total Notes",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = "$totalNotes Notes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Organized in $folderCount folders 📁",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FoldersSection(
    folders: List<NoteFolder>,
    onFolderClick: (String) -> Unit
) {
    Text(
        text = "Folders",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        folders.forEach { folder ->
            FolderCard(folder = folder, onClick = { onFolderClick(folder.name) })
        }
    }
}

@Composable
private fun FolderCard(folder: NoteFolder, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.48f)
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(folder.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = folder.icon,
                    contentDescription = "${folder.name} icon",
                    tint = folder.iconTint
                )
            }

            Text(
                text = folder.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "${folder.count} notes",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

// ---------- recent notes list ----------

@Composable
private fun RecentNotesHeader(
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent Notes",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Text(
            text = "View All",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }
}

@Composable
private fun NoteCard(
    note: NoteItem,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            // top meta row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SubjectTagChip(note.subjectTag)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = note.date,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = note.preview,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                note.tags.forEach { tag ->
                    TagChip(text = "#$tag")
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onEditClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onShareClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Share",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDeleteClick() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFE53935)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectTagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
    }
}

// ---------- bottom actions + tip ----------

@Composable
private fun ImportShareRow(
    onImportClick: () -> Unit,
    onShareAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onImportClick() },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CloudUpload,
                        contentDescription = "Import",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Import Notes",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onShareAllClick() },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share All",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Share All",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
private fun NoteTakingTipCard() {
    val tips = listOf(
        "Use the Cornell Method: Divide your notes into cues, notes, and summary sections for better retention and revision!",
        "End each session with a 3-line summary and a highlight color for action items; revisit within 24 hours.",
        "Keep one note per concept. Add a \"why it matters\" line and one PYQ/MCQ snippet to anchor recall."
    )
    val tipColors = listOf(
        Color(0xFFFF9800),
        Color(0xFF2563EB),
        Color(0xFF0F9D58)
    )
    var tipIndex by remember { mutableStateOf(0) }
    var dragDistance by remember { mutableStateOf(0f) }
    val cardColor by animateColorAsState(
        targetValue = tipColors[tipIndex % tipColors.size],
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 650),
        label = "notes_tip_card_color"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(20_000)
            tipIndex = (tipIndex + 1) % tips.size
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Tip info",
                tint = Color.White,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .pointerInput(tips.size, tipIndex) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    dragDistance > 60 -> tipIndex = (tipIndex - 1 + tips.size) % tips.size
                                    dragDistance < -60 -> tipIndex = (tipIndex + 1) % tips.size
                                }
                                dragDistance = 0f
                            }
                        ) { change, dragAmount ->
                            dragDistance += dragAmount
                            change.consume()
                        }
                    }
            ) {
                Text(
                    text = "📝 Note-taking Tip",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                Text(
                    text = tips[tipIndex],
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White
                    ),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

// ---------- Reference books flow ----------

@Composable
fun NotesReferenceBooksScreen(subjectName: String, navController: NavController) {
    val books = buildReferenceBooks()[subjectName] ?: emptyList()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Reference books & unit lists",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }

        books.forEach { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val s = Uri.encode(subjectName)
                        val b = Uri.encode(book.title)
                        navController.navigate("notes_reference_units/$s/$b")
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = book.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = "${book.units.size} units",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        if (books.isEmpty()) {
            Text(
                text = "Reference books not configured yet.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun NotesReferenceUnitsScreen(subjectName: String, bookTitle: String, navController: NavController) {
    val book = buildReferenceBooks()[subjectName]?.firstOrNull { it.title == bookTitle }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }

        if (book == null) {
            Text(
                text = "Units not found for this book.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            return
        }

        book.units.forEachIndexed { idx, unit ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Unit ${idx + 1}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

private fun buildReferenceBooks(): Map<String, List<ReferenceBook>> {
    fun book(title: String, author: String, summary: String, units: List<String>) =
        ReferenceBook(title, author, summary, units)

    val history = listOf(
        book("Spectrum – Modern India", "Rajiv Ahir", "Modern history quick reference.", listOf("1857 & reforms", "Early Congress", "Gandhian phase", "Post-1947 consolidation")),
        book("India's Struggle for Independence", "Bipan Chandra", "Authoritative freedom struggle narrative.", listOf("Moderates & Extremists", "Mass movements", "Towards partition")),
        book("Ancient & Medieval India", "Poonam Dalal Dahiya", "Comprehensive ancient and medieval coverage.", listOf("Indus & Vedic", "Mahajanapadas to Mauryas", "Sultanate & Mughals", "Culture & society")),
        book("Plassey to Partition", "Sekhar Bandyopadhyay", "Analytical modern India.", listOf("Company rule", "Rise of nationalism", "Partition dynamics")),
        book("History of Modern India", "Bipan Chandra", "Concise colonial impact focus.", listOf("Policies & economy", "National movement phases", "Integration after 1947")),
        book("A Brief History of Modern India", "Spectrum", "Pocket factual recap.", listOf("Reforms & revolts", "INC phases", "Key acts & policies")),
        book("India Since Independence", "Bipan Chandra", "Post-1947 politics & economy.", listOf("Nation building", "Economic policies", "Foreign policy & society"))
    )

    val geography = listOf(
        book("NCERT Fundamentals of Physical Geography", "NCERT", "Physical processes and landforms.", listOf("Earth & tectonics", "Geomorphic processes", "Climatology basics")),
        book("G.C. Leong Physical Geography", "G.C. Leong", "World climates overview.", listOf("Climate controls", "Major climate types", "Soils & biomes")),
        book("India: Physical Environment", "NCERT", "Indian physiography basics.", listOf("Relief & drainage", "Climate & monsoon", "Resources & hazards")),
        book("India: People and Economy", "NCERT", "Human/economic geography of India.", listOf("Resources & agriculture", "Industries & transport", "Population & migration")),
        book("Oxford School Atlas", "Oxford", "Essential map practice.", listOf("India maps", "World maps", "Thematic maps")),
        book("Certificate Physical & Human Geography", "Goh Cheng Leong", "Concise physical geography.", listOf("Geomorphology", "Climates", "Human geography basics")),
        book("Savindra Singh Physical Geography", "Savindra Singh", "Detailed physical geography text.", listOf("Landforms", "Climatology", "Oceanography"))
    )

    val polity = listOf(
        book("Indian Polity", "M. Laxmikanth", "Comprehensive polity reference.", listOf("Framework & features", "Union & State govt", "Local bodies & amendments")),
        book("Introduction to the Constitution of India", "D.D. Basu", "Constitutional commentary.", listOf("Preamble & principles", "Institutions", "Rights & remedies")),
        book("PM Bakshi – Constitution of India", "P.M. Bakshi", "Bare act reference.", listOf("Articles & parts", "Schedules", "Key amendments")),
        book("Our Parliament", "Subhash Kashyap", "Parliament functions simplified.", listOf("Composition", "Procedures", "Committees & officers")),
        book("Governance in India", "M. Laxmikanth", "Governance and e-governance.", listOf("Governance concepts", "Schemes & delivery", "Accountability mechanisms")),
        book("Polity Question Bank", "TMH/Arihant", "Objective practice.", listOf("Static core", "Parliament & state", "Judiciary & bodies")),
        book("Working a Democratic Constitution", "Granville Austin", "Contextual constitutional evolution.", listOf("Constituent Assembly", "Federalism", "Civil liberties"))
    )

    val economy = listOf(
        book("Indian Economy", "Ramesh Singh", "Macro + sectoral overview.", listOf("Growth & development", "Fiscal policy", "Monetary & inflation")),
        book("Indian Economy", "Sanjiv Verma", "Concise prelims-friendly text.", listOf("National income", "Poverty & unemployment", "External sector basics")),
        book("Economic Survey (Key Chapters)", "MoF", "Latest data & themes digest.", listOf("Growth & fiscal", "Sectoral highlights", "Reforms & schemes")),
        book("Sriram IAS Economy Notes", "Sriram IAS", "Coaching-style notes.", listOf("Macro tools", "External sector", "Financial sector")),
        book("NCERT Economics (11-12)", "NCERT", "Foundational micro/macro/IED.", listOf("Micro core", "Macro core", "Indian economic development")),
        book("Indian Economy Key Concepts", "Shankar Ganesh", "Concept + MCQ recap.", listOf("Macro basics", "Sectors & schemes", "External sector")),
        book("Budget & Survey Digest", "Compilation", "Budget math and survey charts.", listOf("Budget math", "Survey themes", "Data deck"))
    )

    val environment = listOf(
        book("Environment", "Shankar IAS", "Flagship environment text.", listOf("Ecology basics", "Biodiversity & PAs", "Pollution & climate")),
        book("PMF IAS Environment", "PMF IAS", "Diagram-rich environment notes.", listOf("Ecology & cycles", "Conventions & acts", "Climate & pollution")),
        book("NCERT Bio Environment Units", "NCERT", "School-level environment grounding.", listOf("Organisms & populations", "Environmental issues", "Conservation basics")),
        book("Majid Husain Environment", "Majid Husain", "UPSC-focused environment coverage.", listOf("Ecology foundations", "Biodiversity", "Climate & disasters")),
        book("Down To Earth Compendium", "CEE/DTL", "Annual environment reports digest.", listOf("Pollution & waste", "Climate & energy", "Biodiversity governance")),
        book("Environmental Studies", "R. Rajagopalan", "Fundamentals with Indian context.", listOf("Ecosystems", "Conservation", "Pollution & policy")),
        book("Report Summaries", "UNEP/WWF/IPCC", "Curated global report notes.", listOf("IPCC/UNEP updates", "WWF/IPBES insights", "India-specific reports"))
    )

    val science = listOf(
        book("NCERT Science (9-10)", "NCERT", "Baseline school science.", listOf("Physics basics", "Chemistry basics", "Biology basics")),
        book("NCERT Science (11-12) Condensed", "NCERT", "Higher-secondary essentials.", listOf("Physics foundations", "Chemistry foundations", "Biology foundations")),
        book("Lucent General Science", "Lucent", "Concise factbook.", listOf("Physics facts", "Chemistry facts", "Biology facts")),
        book("Arihant General Science", "Arihant", "Objective recap + MCQs.", listOf("Physics recap", "Chemistry recap", "Biology recap")),
        book("Science & Technology for UPSC", "TMH/McGraw Hill", "Applied S&T with CA links.", listOf("Space/defence tech", "ICT & emerging tech", "Energy & enviro tech")),
        book("NCERT Exemplar (9-12)", "NCERT", "Problem practice.", listOf("Physics exemplar", "Chemistry exemplar", "Biology exemplar")),
        book("General Science for Civil Services", "Spectrum", "Civil services-oriented summary.", listOf("Physics overview", "Chemistry overview", "Biology & health"))
    )

    return mapOf(
        "History" to history,
        "Geography" to geography,
        "Polity" to polity,
        "Economy" to economy,
        "Environment & Ecology" to environment,
        "Science & Technology" to science
    )
}
