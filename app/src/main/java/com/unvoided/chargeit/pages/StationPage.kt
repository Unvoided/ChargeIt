package com.unvoided.chargeit.pages


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationPage(
    stationsViewModel: StationsViewModel,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
    context: Context
) {
    navBackStackEntry.arguments?.getString("id")?.let { stationId ->
        val stationsObj by stationsViewModel.stationsList.observeAsState()
        var state by remember { mutableStateOf(0) }
        val titles = listOf("Info", "Connections")


        stationsObj?.let { stations ->
            val station = stations.first { it.id == stationId.toInt() }
            Scaffold(
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
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

                    }
                }
            ) { pad ->
                Column(Modifier.padding(pad)) {
                    Card(
                        modifier = Modifier.padding(
                            top = 5.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                .compositeOver(MaterialTheme.colorScheme.surface.copy())
                        )
                    ) {
                        ListItem(
                            modifier = Modifier.padding(10.dp),
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            headlineText = {
                                Text(
                                    text = "${station.operatorInfo!!.title}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }, supportingText = {
                                Text("${station.addressInfo!!.title}")
                                Text("${station.addressInfo!!.town}")

                            }, trailingContent = {
                                Badge {
                                    Text(text = "${station.addressInfo!!.distance?.roundToInt()} km")
                                }
                            }
                        )
                    } // Top Card End
                    Spacer(modifier = Modifier.size(5.dp))
                    Card(
                        modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {


                    }


                }
            }

        }
    }
}