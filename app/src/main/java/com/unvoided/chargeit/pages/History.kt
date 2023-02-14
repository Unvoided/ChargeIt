package com.unvoided.chargeit.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.data.firestore.Users
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.ui.theme.components.LoadingComponent
import com.unvoided.chargeit.ui.theme.components.ShowIfLoggedIn
import com.unvoided.chargeit.ui.theme.components.ShowIfNotEmpty
import kotlinx.coroutines.launch
import java.time.LocalDate

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun History(navController: NavHostController, stationsViewModel: StationsViewModel) {
    ShowIfLoggedIn(navController) {
        val stationsHistory by stationsViewModel.stationsHistory.observeAsState()
        val coroutineScope = rememberCoroutineScope()
        val lazyListState = rememberLazyListState()
        coroutineScope.launch {
            stationsViewModel.fetchStationsHistory()
        }

        LoadingComponent(isLoading = stationsHistory == null) {
            ShowIfNotEmpty(
                icon = Icons.Outlined.History,
                isEmpty = stationsHistory!!.isEmpty(),
                message = "No History"
            ) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(10.dp),
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    stationsHistory?.keys?.forEach { key ->
                        if (stationsHistory!![key]!!.isNotEmpty()) {
                            item {
                                Text(key.toString(), fontWeight = FontWeight.Bold)
                            }
                            stationsHistory!![key]?.forEach {
                                item {
                                    StationsHistoryItem(it, navController, key, stationsViewModel)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsHistoryItem(
    station: Station,
    navController: NavController,
    historyDate: LocalDate,
    stationsViewModel: StationsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
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
                Row {
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
                    IconButton(onClick = {
                        coroutineScope.launch {
                            Users().removeFromHistory(historyDate, station.id!!)
                            stationsViewModel.fetchStationsHistory()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Remove from history"
                        )
                    }
                }

            })
    }

}