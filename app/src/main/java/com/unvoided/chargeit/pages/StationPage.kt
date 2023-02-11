@file:OptIn(ExperimentalMaterial3Api::class)

package com.unvoided.chargeit.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StationPage(
    stationsViewModel: StationsViewModel,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
    context: Context
) {
    navBackStackEntry.arguments?.getString("id")?.let { stationId ->
        val stationsObj by stationsViewModel.stationsList.observeAsState()

        stationsObj?.let { stations ->
            var state by remember { mutableStateOf(0) }
            val station = stations.first { it.id == stationId.toInt() }
            val titles =
                listOf("Info", "Connections (${station.connections?.count() ?: 0})", "Reviews")
            Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
                when (state) {
                    0 -> {
                        Column(horizontalAlignment = Alignment.End) {
                            FloatingActionButton(onClick = {
                                navController.navigate(route = "map?latitude=${station.addressInfo!!.latitude}&longitude=${station.addressInfo!!.longitude}") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }) {
                                Icon(Icons.Default.LocationOn, "Location")
                            }
                            Spacer(Modifier.size(10.dp))
                            FloatingActionButton(onClick = {
                                val gmmIntentUri =
                                    Uri.parse("geo:${station.addressInfo!!.latitude},${station.addressInfo!!.longitude}?q=${station.addressInfo!!.latitude},${station.addressInfo!!.longitude}(${station.operatorInfo!!.title})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                startActivity(context, mapIntent, null)

                            }) {
                                Icon(Icons.Default.Directions, "Navigate on Google Maps")
                            }
                            Spacer(Modifier.size(10.dp))
                            ExtendedFloatingActionButton(
                                onClick = { /*TODO handle used*/ },
                                icon = { Icon(Icons.Outlined.History, "Add to history") },
                                text = {
                                    Text(
                                        text = "Add to History",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                },

                                )
                        }
                    }
                }

            }) { pad ->
                Column(Modifier.padding(pad)) {
                    Card(
                        modifier = Modifier.padding(
                            top = 5.dp
                        ), colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                .compositeOver(MaterialTheme.colorScheme.surface.copy())
                        )
                    ) {
                        ListItem(modifier = Modifier.padding(10.dp),
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            headlineText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${station.operatorInfo!!.title}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text(text = "${station.addressInfo!!.distance?.roundToInt()} km")
                                    }
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Badge(containerColor = if (station.statusType!!.isOperational!!) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) {
                                        Text(
                                            text = if (station.statusType?.isOperational == null) {
                                                "Unknown"
                                            } else {
                                                if (station.statusType!!.isOperational!!) {
                                                    "Operational"
                                                } else {
                                                    "Not Operational"
                                                }
                                            }
                                        )
                                    }
                                }

                            },
                            supportingText = {
                                Text("${station.addressInfo!!.title}")
                                Text("${station.addressInfo!!.town}")
                            },
                            trailingContent = {
                                IconButton(onClick = {/*TODO handle favorite*/ }) {
                                    Icon(Icons.Outlined.Favorite, "Favorite")
                                }
                            })
                    } // Top Card End
                    Spacer(modifier = Modifier.size(5.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = 5.dp
                            ), colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                .compositeOver(MaterialTheme.colorScheme.surface.copy())
                        )
                    ) {
                        TabRow(selectedTabIndex = state, containerColor = Color.Transparent) {
                            titles.forEachIndexed { index, title ->
                                Tab(selected = state == index, onClick = { state = index }, text = {
                                    Text(
                                        text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                                    )
                                })
                            }
                        }

                        when (state) {
                            0 -> {
                                InfoTab(station)
                            }
                            1 -> {
                                ChargersTab(station)
                            }
                            2 -> {
                                ReviewsTab(station)
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun InfoTab(station: Station) {
    station.usageCost?.let {
        ListItem(headlineText = {
            Text("Usage Cost", fontWeight = FontWeight.Bold)
        }, supportingText = {
            Text(
                modifier = Modifier.padding(5.dp),
                text = it,
                style = MaterialTheme.typography.labelLarge
            )
        })
    }
    Divider()
    station.numberOfPoints?.let {
        ListItem(
            headlineText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Number of Points",
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(text = "$it")
                    }
                }
            },
        )
    }
    Divider()
    station.operatorInfo?.let {
        if (it.contactEmail != null || it.phonePrimaryContact != null) ListItem(headlineText = {
            Text("Contact", fontWeight = FontWeight.Bold)
        }, supportingText = {
            Row(modifier = Modifier.padding(5.dp)) {
                it.phonePrimaryContact?.let { number ->

                    Text(
                        text = "Phone number:", fontWeight = FontWeight.Bold
                    )
                    Text(text = " $number")

                }
                it.contactEmail?.let { email ->
                    Text(
                        text = "Email:", fontWeight = FontWeight.Bold
                    )
                    Text(text = " $email")
                }
            }

        })
    }

}

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
                            Badge(containerColor = if (it.statusType!!.isOperational!!) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) {
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
                            Text(
                                text = "Amps: ",
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${it.amps}A",
                            )
                        }
                        Row {
                            Text(
                                text = "Voltage: ",
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${it.voltage}V",
                            )
                        }
                        Row {
                            Text(
                                text = "Power: ",
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${it.powerKw}kW",
                            )
                        }
                    }
                })
                Divider()
            }
        }
    }
}

@Composable
fun ReviewsTab(station: Station) {
    Text("test3")
}
