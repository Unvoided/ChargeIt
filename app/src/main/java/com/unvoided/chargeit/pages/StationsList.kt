package com.unvoided.chargeit.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.viewmodels.LocationViewModel
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import kotlin.math.roundToInt

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

    Scaffold() { padding ->

        AnimatedVisibility(
            visible = stationsList?.isEmpty() ?: true,
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
                    "Loading stations", textAlign = TextAlign.Center
                )
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            stationsList?.forEach { station ->

                item {
                    StationItem(station, navController)
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationItem(station: Station, navController: NavController) {
    ListItem(modifier = Modifier.clickable { }, headlineText = {
        Text(
            text = "${station.operatorInfo!!.title}", fontWeight = FontWeight.Bold
        )
    }, supportingText = {
        Text("${station.addressInfo!!.town}")

    }, trailingContent = {
        FilledTonalIconButton(
            onClick = { navController.navigate(route = "map?latitude=${station.addressInfo!!.latitude}&longitude=${station.addressInfo!!.longitude}") },
        ) {
            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Show on Map")
        }
    }, leadingContent = {
        Badge {
            Text(text = "${station.addressInfo!!.distance?.roundToInt()} km")
        }
    })
}