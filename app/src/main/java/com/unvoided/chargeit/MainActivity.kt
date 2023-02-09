@file:OptIn(ExperimentalMaterial3Api::class)

package com.unvoided.chargeit

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.unvoided.chargeit.data.LocationViewModel
import com.unvoided.chargeit.pages.ChargersMap
import com.unvoided.chargeit.pages.Favorites
import com.unvoided.chargeit.pages.History
import com.unvoided.chargeit.ui.theme.ChargeItTheme

class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var listeningToUpdates = false

    private val locationCallback: LocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.locations.first()
            locationViewModel.updateLocation(location)
            Log.d("Location#Callback", location.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startUpdatingLocation()

        setContent {
            ChargeItTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        topBar = { ChargeItTopBar() },
                        bottomBar = { ChargeItNavBar(navController) }) { paddingValues ->
                        ChargeItNavHost(
                            navController = navController,
                            paddingValues = paddingValues,
                            locationViewModel = locationViewModel
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 0)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdatingLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            listeningToUpdates = true
        }.addOnFailureListener { e ->
            Log.d("Location", "Unable to get location", e)
        }
    }

    override fun onStop() {
        super.onStop()
        if (listeningToUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate()
        }
    }
}

@Composable
fun ChargeItTopBar() {
    TopAppBar(
        title = { Text("ChargeIt", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                .compositeOver(MaterialTheme.colorScheme.surface.copy())
        ),
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                )
            }
        }
    )

}

@Composable
fun ChargeItNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    locationViewModel: LocationViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Pages.ChargersMapPage.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Pages.ChargersMapPage.route) { ChargersMap(locationViewModel, paddingValues) }
        composable(Pages.HistoryPage.route) { History() }
        composable(Pages.FavoritesPage.route) { Favorites() }
    }
}

@Composable
fun ChargeItNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        Pages.values().forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )

        }

    }
}

enum class Pages(val route: String, val label: String, val icon: ImageVector) {
    HistoryPage("history", "History", Icons.Filled.History),
    ChargersMapPage("map", "Map", Icons.Filled.Map),
    FavoritesPage("favorites", "Favorites", Icons.Filled.Favorite)
}
