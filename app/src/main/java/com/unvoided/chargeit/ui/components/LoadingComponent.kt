package com.unvoided.chargeit.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoadingComponent(
    isLoading: Boolean,
    loadingMessage: String? = null,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        ElevatedCard(
            modifier = Modifier
                .wrapContentSize()
                .padding(20.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.size(10.dp))
                loadingMessage?.let {
                    Text(
                        it,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        content()
    }
}
