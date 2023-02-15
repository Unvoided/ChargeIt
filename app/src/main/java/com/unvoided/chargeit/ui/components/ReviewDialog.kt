package com.unvoided.chargeit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unvoided.chargeit.data.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDialog(
    dialogState: MutableState<Boolean>,
    message: String,
    oldReview: Review? = null,
    onReviewSubmit: (Review, Review?) -> Unit
) {
    if (dialogState.value) {
        val ratingOptions = listOf(1, 2, 3, 4, 5)
        var selectedRating by remember { mutableStateOf(ratingOptions[0]) }


        var text by rememberSaveable { mutableStateOf(oldReview?.comment ?: "") }
        var expanded by remember { mutableStateOf(false) }
        var textError by remember { mutableStateOf(false) }
        AlertDialog(onDismissRequest = { dialogState.value = false }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        message,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        isError = textError,
                        label = { Text("Comment") },
                    )
                    Spacer(Modifier.height(10.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }) {

                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = selectedRating.toString(),
                            onValueChange = {},
                            label = { Text("Rating") },
                            leadingIcon = {
                                Icon(Icons.Filled.Star, "Rating")
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )

                        val filteredOptions = ratingOptions.filter { it != selectedRating }
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            filteredOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption.toString()) },
                                    onClick = {
                                        selectedRating = selectionOption
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                dialogState.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                if (text.isEmpty()) {
                                    textError = true
                                } else {
                                    onReviewSubmit(
                                        Review(
                                            userUid = Firebase.auth.currentUser!!.uid,
                                            userName = Firebase.auth.currentUser!!.displayName!!,
                                            userPictureUrl = Firebase.auth.currentUser!!.photoUrl?.toString(),
                                            timestamp = Timestamp.now(),
                                            rating = selectedRating,
                                            comment = text,
                                        ),
                                        oldReview
                                    )
                                    dialogState.value = false
                                }
                            }
                        ) {
                            Text("Submit")
                        }
                    }

                }
            }
        }
    }

}