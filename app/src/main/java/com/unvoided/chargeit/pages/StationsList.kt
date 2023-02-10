package com.unvoided.chargeit.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unvoided.chargeit.data.viewmodels.LocationViewModel
import com.unvoided.chargeit.data.viewmodels.StationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsList(
    locationViewModel: LocationViewModel,
    stationsViewModel: StationsViewModel,
    paddingValues: PaddingValues,
    navController: NavController
) {
    val lazyListState = rememberLazyListState()

    val stationsList by stationsViewModel.stationsList.observeAsState()
    val location by locationViewModel.location.observeAsState()

    Scaffold(
        topBar = {
            Text("Nearby Stations")
        }
    ) { padding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            stationsList?.forEach { station ->
                item { Text(text = "${station.station.distance}") }
            }

        }
    }
}