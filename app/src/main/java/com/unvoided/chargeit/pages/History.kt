package com.unvoided.chargeit.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.unvoided.chargeit.pages.components.ShowIfLoggedIn

@Composable
fun History(navController: NavHostController) {
    ShowIfLoggedIn(navController) {
        Text("History ${it.displayName}")
    }
}