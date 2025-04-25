package com.example.lightfilm.Helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun DeletionDialog(dialogIsOpen: MutableState<Boolean>, text: String, onConfirmation: () -> Unit) {
    if (dialogIsOpen.value) {
        AlertDialog(
            text = {
                Text(text)
            },
            icon = {
                Icon(Icons.Default.Delete, contentDescription = null)
            },
            onDismissRequest = {
                dialogIsOpen.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmation
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogIsOpen.value = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}