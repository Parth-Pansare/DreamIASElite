package com.app.dreamiaselite.ui.screen.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.app.dreamiaselite.ui.theme.Gold

@Composable
fun HelpFeedbackScreen() {
    var message by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Help & Feedback",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Facing an issue or have a feature idea? Tell us and we’ll try to make it happen.",
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
                    text = "Contact us",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Describe your issue or feedback") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { /* TODO: send to backend or email */ },
                    modifier = Modifier.align(androidx.compose.ui.Alignment.End),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = LightSurface
                    )
                ) {
                    Text("Submit")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Quick help",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "• If tests are not loading, check your internet connection.\n" +
                            "• For login issues, make sure you’re using the latest app version.\n" +
                            "• You can clear app data & re-login if content looks outdated.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondary
                    )
                )
            }
        }

        Text(
            text = "Response time: typically within 24–48 hours (future feature).",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary
            )
        )
    }
}
