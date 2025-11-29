@file:OptIn(ExperimentalLayoutApi::class)

package com.app.dreamiaselite.ui.screen.screens.tests

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Science
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class SubjectCardData(
    val name: String,
    val books: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color,
    val bubble: Color
)

@Composable
fun TestsScreen(navController: NavController) {
    // Remember subjects list to prevent recreation on each recomposition
    val subjects = remember {
        listOf(
            SubjectCardData("History", "4 books", Icons.Outlined.MenuBook, Color(0xFF3B6EFF), Color(0xFFE7F0FF)),
            SubjectCardData("Geography", "3 books", Icons.Outlined.Public, Color(0xFF2F9D6B), Color(0xFFE5F5EC)),
            SubjectCardData("Polity", "3 books", Icons.Outlined.AccountBalance, Color(0xFFB36AF7), Color(0xFFF3E9FF)),
            SubjectCardData("Economy", "3 books", Icons.Outlined.Savings, Color(0xFFF5A524), Color(0xFFFFF4E3)),
            SubjectCardData("Environment & Ecology", "3 books", Icons.Outlined.Eco, Color(0xFF2CB174), Color(0xFFE6F8EE)),
            SubjectCardData("Science & Technology", "3 books", Icons.Outlined.Science, Color(0xFFDF6CC6), Color(0xFFFCEBFF))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Prelims Preparation",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "Select a subject to view resources",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        )

        HighlightCard()

        FlowRow(
            maxItemsInEachRow = 2,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            subjects.forEach { subject ->
                SubjectCard(
                    subject = subject,
                    modifier = Modifier.fillMaxWidth(0.48f),
                    onClick = {
                        val encoded = Uri.encode(subject.name)
                        navController.navigate("test_subject/$encoded")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(80.dp)) // avoid bottom-bar overlap
    }
}

@Composable
private fun HighlightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F73FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Choose Your Subject",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
            Text(
                text = "Select a subject to explore recommended reference books and practice tests for UPSC Prelims",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
        }
    }
}

@Composable
private fun SubjectCard(subject: SubjectCardData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(subject.bubble),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = subject.name,
                    tint = subject.tint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = subject.books,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)),
                textAlign = TextAlign.Center
            )
        }
    }
}
