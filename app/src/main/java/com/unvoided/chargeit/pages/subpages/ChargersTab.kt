package com.unvoided.chargeit.pages.subpages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unvoided.chargeit.data.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargersTab(station: Station) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState
    ) {
        station.connections?.forEach {
            item {
                ListItem(headlineText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${it.connectionType?.title}",
                            fontWeight = FontWeight.Bold,
                        )
                        Row {
                            it.quantity?.let { qty ->
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text(text = "$qty")
                                }
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                            Badge(containerColor = if (it.statusType?.isOperational == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) {
                                Text(
                                    text = if (it.statusType?.isOperational == null) {
                                        "Unknown"
                                    } else {
                                        if (it.statusType!!.isOperational!!) {
                                            "Operational"
                                        } else {
                                            "Not Operational"
                                        }
                                    }
                                )
                            }
                        }

                    }
                }, supportingText = {
                    Text(
                        text = "${it.connectionType?.formalName}",
                    )
                    Column(modifier = Modifier.padding(top = 5.dp)) {
                        Row {
                            it.amps?.let {
                                Text(
                                    text = "Amps: ",
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "${it}A",
                                )
                            }
                        }
                        Row {
                            it.voltage?.let {
                                Text(
                                    text = "Voltage: ",
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "${it}V",
                                )
                            }
                        }
                        Row {
                            it.powerKw?.let {
                                Text(
                                    text = "Power: ",
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "${it}kW",
                                )
                            }
                        }
                    }
                })
                Divider()
            }
        }
    }
}