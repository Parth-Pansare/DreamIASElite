package com.app.dreamiaselite.ui.screen.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dreamiaselite.ui.theme.LightBackground
import com.app.dreamiaselite.ui.theme.LightSurface
import com.app.dreamiaselite.ui.theme.TextPrimary
import com.app.dreamiaselite.ui.theme.TextSecondary

@Composable
fun SettingsScreen() {
    var notifDailyReminders by remember { mutableStateOf(true) }
    var notifTestReminders by remember { mutableStateOf(true) }
    var notifNews by remember { mutableStateOf(false) }
    var wifiOnlyDownloads by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Control notifications, downloads and reminders.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary
            )
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                SettingsToggleRow(
                    title = "Daily study reminder",
                    description = "Get a reminder at your preferred time.",
                    checked = notifDailyReminders,
                    onCheckedChange = { notifDailyReminders = it }
                )

                Divider()

                SettingsToggleRow(
                    title = "Test series reminders",
                    description = "Remind me before my scheduled tests.",
                    checked = notifTestReminders,
                    onCheckedChange = { notifTestReminders = it }
                )

                Divider()

                SettingsToggleRow(
                    title = "Current affairs alerts",
                    description = "Important CA updates & compilations.",
                    checked = notifNews,
                    onCheckedChange = { notifNews = it }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Downloads & storage",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                SettingsToggleRow(
                    title = "Download on Wi-Fi only",
                    description = "Avoid using mobile data for large files.",
                    checked = wifiOnlyDownloads,
                    onCheckedChange = { wifiOnlyDownloads = it }
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Storage usage: 0.0 MB (for now)",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
