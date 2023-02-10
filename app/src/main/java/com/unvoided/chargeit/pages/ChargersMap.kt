package com.unvoided.chargeit.pages

import androidx.compose.animation.AnimatedVisibility
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.unvoided.chargeit.data.LocationViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargersMap(locationViewModel: LocationViewModel, paddingValues: PaddingValues) {
    val locationObj by locationViewModel.location.observeAsState()

    AnimatedVisibility(visible = locationObj == null) {
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

    locationObj?.let {
        val latitude = it.latitude
        val longitude = it.longitude
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 16f)
        }
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { /*TODO*/ },
                        icon = { Icon(Icons.Outlined.Power, "Find Chargers") },
                        text = { Text(text = "Find Chargers") },
                    )
                    FloatingActionButton(
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
                        }) {
                        Icon(Icons.Outlined.MyLocation, "My Location")
                    }
                }

            }
        ) { padding ->

            GoogleMap(
                modifier = Modifier.padding(padding),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = false,
                    myLocationButtonEnabled = false,
                ),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                )
            ) {
            }

        }

    }

}