@file:OptIn(ExperimentalLayoutApi::class)
package com.app.dreamiaselite.ui.screen.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextPrimary

// ---------- data models ----------

data class NoteFolder(
    val name: String,
    val count: Int,
    val color: Color
)

data class NoteItem(
    val id: Int,
    val subjectTag: String, // e.g. "Polity", "CA"
    val date: String,       // "Nov 16"
    val title: String,
    val preview: String,
    val tags: List<String>
)

// ---------- main screen ----------

@Composable
fun NotesScreen() {
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }

    // mock folders
    val folders = remember {
        listOf(
            NoteFolder("History", 24, Color(0xFFEBF2FF)),
            NoteFolder("Polity", 18, Color(0xFFF3E7FF)),
            NoteFolder("Geography", 31, Color(0xFFE7F7EE)),
            NoteFolder("Economy", 15, Color(0xFFFFF0E2))
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
                NotesHeader(
                    onNewNoteClick = {
                        // TODO: open create note screen
                    }
                )
                Spacer(Modifier.height(12.dp))

                NotesSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )

                Spacer(Modifier.height(16.dp))

                TotalNotesCard(
                    totalNotes = notes.size,
                    folderCount = folders.size
                )

                Spacer(Modifier.height(16.dp))

                FoldersSection(folders)

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
    onNewNoteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Notes",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Button(
            onClick = onNewNoteClick,
            modifier = Modifier
                .wrapContentWidth()
                .height(36.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = LightSurface
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 12.dp,
                vertical = 0.dp
            )
        ) {
            Text(
                text = "+ New Note",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun NotesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search notes...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary.copy(alpha = 0.5f)
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
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                // simple document icon block
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
    folders: List<NoteFolder>
) {
    Text(
        text = "Folders",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        folders.forEach { folder ->
            FolderCard(folder = folder)
        }
    }
}

@Composable
private fun FolderCard(folder: NoteFolder) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.48f)
            .wrapContentHeight()
            .clickable { /* TODO: open folder */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(folder.color),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                )
            }

            Text(
                text = folder.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            )
            Text(
                text = "${folder.count} notes",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary.copy(alpha = 0.7f)
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
                color = TextPrimary
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
        colors = CardDefaults.cardColors(containerColor = LightSurface),
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
                            color = TextPrimary.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )

            Text(
                text = note.preview,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary.copy(alpha = 0.7f)
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
                            tint = TextPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextPrimary
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
                            tint = TextPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Share",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextPrimary
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
            .background(Color(0xFFEAF0FF))
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
            .background(Color(0xFFF3F4F8))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary.copy(alpha = 0.8f)
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
            color = LightSurface,
            tonalElevation = 1.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share, // placeholder icon
                    contentDescription = "Import",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Import Notes",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onShareAllClick() },
            shape = RoundedCornerShape(16.dp),
            color = LightSurface,
            tonalElevation = 1.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share All",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Share All",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun NoteTakingTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9800)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "📝 Note-taking Tip",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            Text(
                text = "Use the Cornell Method: Divide your notes into cues, notes, and summary sections for better retention and revision!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                )
            )
        }
    }
}
