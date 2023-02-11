package com.unvoided.chargeit.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.unvoided.chargeit.data.viewmodels.LocationViewModel
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.retrofit.GetStationsInput
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsMap(
    locationViewModel: LocationViewModel,
    stationsViewModel: StationsViewModel,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry
) {
    val locationObj by locationViewModel.location.observeAsState()
    val stationsList by stationsViewModel.stationsList.observeAsState()

    AnimatedVisibility(
        visible = locationObj == null,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                "Loading map \n(Check if location services are enabled)",
                textAlign = TextAlign.Center
            )
        }
    }

    AnimatedVisibility(
        visible = locationObj != null,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {

        val latitude = locationObj!!.latitude
        val longitude = locationObj!!.longitude
        val coroutineScope = rememberCoroutineScope()
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 16f)
        }

        navBackStackEntry.arguments?.let {
            if (it.containsKey("latitude") && it.containsKey("longitude")) {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.getString("latitude")!!.toDouble(),
                                it.getString("longitude")!!.toDouble()
                            ),
                            16f
                        ),
                        durationMs = 500
                    )
                    navBackStackEntry.arguments!!.clear()
                }
            }
        }

        stationsViewModel.fetchStations(
            GetStationsInput(
                latitude = latitude,
                longitude = longitude
            )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    FilledIconButton(
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            latitude,
                                            longitude
                                        ),
                                        16f
                                    ),
                                    durationMs = 500
                                )
                            }
                        },
                        Modifier.size(50.dp)
                    ) {
                        Icon(Icons.Outlined.MyLocation, "My Location")
                    }
                    Spacer(Modifier.size(10.dp))
                    ExtendedFloatingActionButton(
                        onClick = {
                            navController.navigate("map/stations") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.Power, "Find Stations") },
                        text = {
                            Text(
                                text = "Find Stations",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                    )
                }

            }
        ) { padding ->

            GoogleMap(
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = false,
                    myLocationButtonEnabled = false,
                ),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                ),
                contentPadding = padding
            ) {

                stationsList?.forEach { station ->

                    Marker(
                        title = station.operatorInfo!!.title!!,
                        state = MarkerState(
                            LatLng(
                                station.addressInfo!!.latitude!!,
                                station.addressInfo!!.longitude!!
                            )
                        ),
                        onClick = {
                            navController.navigate(route = "map/stations/${station.id}") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                            true
                        }
                    ) {
                    }
                }
            }

        }

    }


}