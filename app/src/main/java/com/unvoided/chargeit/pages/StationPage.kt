@file:OptIn(ExperimentalMaterial3Api::class)

package com.unvoided.chargeit.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unvoided.chargeit.data.firestore.Users
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.pages.subpages.ChargersTab
import com.unvoided.chargeit.pages.subpages.InfoTab
import com.unvoided.chargeit.pages.subpages.ReviewsTab
import com.unvoided.chargeit.ui.theme.components.LoadingComponent
import kotlinx.coroutines.launch
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
        stationsViewModel.fetchStationById(stationId.toInt())
        val stationObj by stationsViewModel.station.observeAsState()

        LoadingComponent(
            isLoading = stationObj == null || stationObj!!.id != stationId.toInt(),
            loadingMessage = "Loading Station Page"
        ) {
            stationObj?.let { station ->
                val coroutineScope = rememberCoroutineScope()
                var state by remember { mutableStateOf(0) }
                var isFavorite by remember { mutableStateOf(false) }

                coroutineScope.launch {
                    if (Firebase.auth.currentUser != null) {
                        isFavorite = Users().isFavorite(stationId.toInt())
                    }
                }

                val titles =
                    listOf("Info", "Connections (${station.connections?.count() ?: 0})", "Reviews")

                Scaffold(floatingActionButtonPosition = FabPosition.End,
                    floatingActionButton = {
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
                                    }) {/*TODO handle used*/
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
                        ElevatedCard(
                            modifier = Modifier.padding(
                                top = 5.dp
                            )
                        ) {
                            ListItem(
                                modifier = Modifier.padding(10.dp),
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                ),
                                headlineText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "${station.operatorInfo!!.title}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.headlineLarge,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Spacer(modifier = Modifier.size(10.dp))
                                        station.addressInfo?.distance?.let {
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                Text(text = "${it.roundToInt()} km")
                                            }
                                            Spacer(modifier = Modifier.size(10.dp))
                                        }
                                        Badge(containerColor = if (station.statusType?.isOperational == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) {
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
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            handleFavorite(
                                                context,
                                                stationId.toInt(),
                                            ) { updateFav: Boolean -> isFavorite = updateFav }
                                        }
                                    }) {
                                        Icon(
                                            if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                            "Favorite"
                                        )
                                    }
                                })
                        } // Top Card End
                        Spacer(modifier = Modifier.size(5.dp))
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    bottom = 5.dp
                                )
                        ) {
                            TabRow(selectedTabIndex = state, containerColor = Color.Transparent) {
                                titles.forEachIndexed { index, title ->
                                    Tab(selected = state == index,
                                        onClick = { state = index },
                                        text = {
                                            Text(
                                                text = title,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
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
                                    ReviewsTab(navController, station)
                                }
                            }
                        }
                    }

                }
            }
        }

    }
}



suspend fun handleFavorite(
    context: Context,
    stationId: Int,
    toggleFavouriteCallback: (Boolean) -> Unit
) {
    val user = Firebase.auth.currentUser

    if (user == null) {
        Toast.makeText(
            context, "You need to be logged in to do that!", Toast.LENGTH_SHORT
        ).show()
        return
    }

    val userDbActions = Users()

    if (userDbActions.isFavorite(stationId)) {
        userDbActions.removeFavorite(stationId)
    } else {
        userDbActions.addFavorite(stationId)
    }

    toggleFavouriteCallback(userDbActions.isFavorite(stationId))
}


