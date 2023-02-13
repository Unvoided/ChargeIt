@file:OptIn(ExperimentalMaterial3Api::class)

package com.unvoided.chargeit

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unvoided.chargeit.data.viewmodels.LocationViewModel
import com.unvoided.chargeit.data.viewmodels.StationsViewModel
import com.unvoided.chargeit.pages.*
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
                    val stationsViewModel: StationsViewModel = viewModel()
                    Scaffold(
                        topBar = { ChargeItTopBar(navController) },
                        bottomBar = { ChargeItNavBar(navController) }) { paddingValues ->
                        ChargeItNavHost(
                            navController = navController,
                            paddingValues = paddingValues,
                            stationsViewModel = stationsViewModel,
                            locationViewModel = locationViewModel,
                            context = this
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
fun ChargeItTopBar(navController: NavController) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    Firebase.auth.addAuthStateListener {
        user = it.currentUser
    }

    val previousBackStackEntry by navController.currentBackStackEntryAsState()
    TopAppBar(
        title = { Text("ChargeIt", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                .compositeOver(MaterialTheme.colorScheme.surface.copy())
        ),
        navigationIcon = {
            if ((previousBackStackEntry?.destination?.hierarchy?.count {
                    !Pages.values().any { page -> page.route == it.route }
                } ?: 0) > 1) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                if (user != null && user?.photoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            user!!.photoUrl
                        ),
                        contentDescription = "Account",
                        Modifier
                            .clip(CircleShape)
                            .size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account"
                    )
                }
            }
        }
    )

}

@Composable
fun ChargeItNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    stationsViewModel: StationsViewModel,
    locationViewModel: LocationViewModel,
    context: Context
) {
    NavHost(
        navController = navController,
        startDestination = Pages.StationsMapPage.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(
            route = Pages.StationsMapPage.route, arguments = listOf(
                navArgument("latitude") {
                    nullable = true
                    type = NavType.StringType
                },
                navArgument("longitude") {
                    nullable = true
                    type = NavType.StringType
                },
            )
        ) { navBackStackEntry ->
            StationsMap(
                locationViewModel,
                stationsViewModel,
                navController,
                navBackStackEntry
            )
        }
        composable("map/stations") {
            StationsList(
                stationsViewModel,
                navController,
            )
        }
        composable("map/stations/{id}") { navBackStackEntry ->
            StationPage(
                stationsViewModel,
                navController,
                navBackStackEntry,
                context
            )
        }
        composable(Pages.HistoryPage.route) { History(navController) }
        composable(Pages.FavoritesPage.route) { Favorites(navController) }
        composable("profile") { Profile() }
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
    StationsMapPage("map?latitude={latitude}&longitude={longitude}", "Map", Icons.Filled.Map),
    FavoritesPage("favorites", "Favorites", Icons.Filled.Favorite)
}
