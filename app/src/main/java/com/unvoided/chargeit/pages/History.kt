package com.unvoided.chargeit.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.unvoided.chargeit.pages.components.ShowIfLoggedIn

@Composable
fun History(navController: NavHostController) {
    val id = 235033
    ShowIfLoggedIn(navController) {
        IconButton(onClick = {
            navController.navigate("map/stations/${id}") {
                popUpTo("history") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }) {
            Icon(Icons.Default.Home, "Home")
        }
    }
}