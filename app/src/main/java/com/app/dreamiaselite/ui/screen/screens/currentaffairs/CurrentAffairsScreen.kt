package com.app.dreamiaselite.ui.screens.currentaffairs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dreamiaselite.ui.theme.AccentCyan
import com.app.dreamiaselite.ui.theme.LightBackground
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextSecondary

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
        CurrentAffairItem("India chairs global climate summit", "17 Nov 2025", "Environment")
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AccentCyan.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.tag,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = AccentCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
