package com.focusgram.client.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun MindfulBreakDialog(
    elapsedMinutes: Int,
    onCloseApp: () -> Unit,
    onExtendFiveMinutes: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(text = "Waktunya Istirahat Sejenak")
        },
        text = {
            Text(
                text = "Anda sudah menjelajah selama $elapsedMinutes menit dalam sesi ini. " +
                    "Berhenti sejenak membantu menjaga fokus dan ketenangan pikiran Anda."
            )
        },
        confirmButton = {
            Button(onClick = onCloseApp) {
                Text("Tutup Aplikasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onExtendFiveMinutes) {
                Text("Tambah 5 Menit")
            }
        }
    )
}
