package com.example.dog.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            DogImageScreen(navController = navController)
        }
        composable("breed/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            BreedDetailScreen(breedId = id!!)
        }
    }
}