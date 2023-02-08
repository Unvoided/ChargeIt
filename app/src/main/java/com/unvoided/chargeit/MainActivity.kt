@file:OptIn(ExperimentalMaterial3Api::class)

package com.unvoided.chargeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unvoided.chargeit.ui.theme.ChargeItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            paddingValues = paddingValues
                        )
                    }
                }
            }
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
fun ChargeItNavHost(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "test1",
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("test1") { Text("Hello1") }
        composable("test2") { Text("Hello2") }
        composable("test3") { Text("Hello3") }
    }
}

@Composable
fun ChargeItNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val chargeItNavigationItems = listOf("test1", "test2", "test3")

    NavigationBar {
        chargeItNavigationItems.forEachIndexed { _, item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item } == true

            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = {
                    Text(
                        text = item,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item) {
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

