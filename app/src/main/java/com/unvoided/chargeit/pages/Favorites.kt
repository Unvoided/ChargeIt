package com.unvoided.chargeit.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.unvoided.chargeit.pages.components.ShowIfLoggedIn

@Composable
fun Favorites(navController: NavHostController) {
    ShowIfLoggedIn(navController) {
        Text("Favourites ${it.displayName}")
    }
}