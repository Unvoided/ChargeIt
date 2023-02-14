package com.unvoided.chargeit.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ShowIfNotEmpty(
    icon: ImageVector,
    isEmpty: Boolean,
    message: String? = null,
    content: @Composable () -> Unit
) {
    if (isEmpty) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                icon,
                "No Content",
                modifier = Modifier.size(150.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.size(10.dp))
            message?.let {
                Text(
                    it,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        content()
    }
}