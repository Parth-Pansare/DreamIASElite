package com.app.dreamiaselite.ui.screen.screens.currentaffairs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import com.app.dreamiaselite.ui.theme.LightBackground
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextSecondary
import com.app.dreamiaselite.ui.theme.TagBackground
import androidx.compose.foundation.shape.RoundedCornerShape


data class CurrentAffairItem(
    val title: String,
    val date: String,
    val tag: String
)

@Composable
fun CurrentAffairsScreen() {
    val items = listOf(
        CurrentAffairItem("SC ruling on Data Protection Act", "18 Nov 2025", "Polity"),
        CurrentAffairItem("RBI hikes Repo Rate by 25 bps", "18 Nov 2025", "Economy"),
        CurrentAffairItem("India chairs climate summit", "17 Nov 2025", "Environment")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Current Affairs",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Daily curated topics relevant for UPSC preparation.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary,
                fontSize = 14.sp
            )
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                CurrentAffairCard(item)
            }
        }
    }
}

@Composable
private fun CurrentAffairCard(item: CurrentAffairItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
                TagChip(text = item.tag)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TagBackground)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
