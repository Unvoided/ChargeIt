package com.unvoided.chargeit.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicActionDialog(openDialog: MutableState<Boolean>, message: String, onSubmit: () -> Unit) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(message)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text("No")
                        }
                        TextButton(
                            onClick = {
                                onSubmit()
                            }
                        ) {
                            Text("Yes")
                        }
                    }

                }
            }
        }
    }
}