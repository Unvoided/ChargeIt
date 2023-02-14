package com.unvoided.chargeit.pages


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.ui.theme.components.LoadingComponent
import kotlin.math.roundToInt

@Composable
fun StationsList(
    stationsViewModel: StationsViewModel,
    navController: NavController
) {
    val lazyListState = rememberLazyListState()

    val stationsList by stationsViewModel.stationsList.observeAsState()


    LoadingComponent(
        isLoading = stationsList?.isEmpty() ?: true,
        loadingMessage = "Loading Stations"
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .fillMaxSize(),
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize(),
        onClick = {
            navController.navigate(route = "map/stations/${station.id!!}") {
                popUpTo("map/stations") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    ) {
        ListItem(
            modifier = Modifier.padding(10.dp),
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            headlineText = {
                Text(
                    text = "${station.operatorInfo!!.title}", fontWeight = FontWeight.Bold
                )
            }, supportingText = {
                Text("${station.addressInfo!!.title}")
                Text("${station.addressInfo!!.town}")

            }, trailingContent = {
                FilledTonalIconButton(
                    onClick = {
                        navController.navigate(route = "map?latitude=${station.addressInfo!!.latitude}&longitude=${station.addressInfo!!.longitude}") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Show on Map"
                    )
                }
            }, leadingContent = {
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(text = "${station.addressInfo!!.distance?.roundToInt()} km")
                }
            })
    }

}