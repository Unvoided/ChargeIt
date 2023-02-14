package com.unvoided.chargeit.pages.subpages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.unvoided.chargeit.data.Station
import com.unvoided.chargeit.ui.theme.components.ShowIfLoggedIn

@Composable
fun ReviewsTab(navController: NavHostController, station: Station) {
    ShowIfLoggedIn(navController) {
        Text("Reviews ${it.displayName}")
    }
}