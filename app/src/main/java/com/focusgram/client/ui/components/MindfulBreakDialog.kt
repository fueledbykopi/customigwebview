package com.focusgram.client.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.focusgram.client.ui.theme.LocalFocusThemeTokens

@Composable
fun MindfulBreakDialog(
    elapsedMinutes: Int,
    onCloseApp: () -> Unit,
    onExtendFiveMinutes: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalFocusThemeTokens.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = tokens.glassSheet,
        icon = {
            Icon(
                imageVector = FocusIcons.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "Waktunya Istirahat Sejenak",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Anda sudah menjelajah selama $elapsedMinutes menit dalam sesi ini. " +
                    "Berhenti sejenak membantu menjaga fokus dan ketenangan pikiran Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onCloseApp,
                shape = CircleShape
            ) {
                Text("Tutup Aplikasi")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onExtendFiveMinutes,
                shape = CircleShape
            ) {
                Text("Tambah 5 Menit", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
