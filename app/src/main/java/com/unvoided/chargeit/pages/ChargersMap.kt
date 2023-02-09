package com.unvoided.chargeit.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.unvoided.chargeit.data.LocationViewModel

@Composable
fun ChargersMap(locationViewModel: LocationViewModel) {
    val currentLocation by locationViewModel.location.observeAsState()

    Text("Latitute: ${currentLocation?.latitude}, Longitude: ${currentLocation?.longitude}")
}