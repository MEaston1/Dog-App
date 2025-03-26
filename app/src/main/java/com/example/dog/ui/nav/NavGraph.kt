package com.example.dog.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dog.ui.BreedDetailScreen
import com.example.dog.ui.DogImageScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            DogImageScreen(navController = navController)
        }
        composable("favourites") {
            //FavouritesScreen(navController = navController)
        }
        composable(
            route = "breed/{breedId}",
            arguments = listOf(navArgument("breedId") { type = NavType.StringType })
        ) { backStackEntry ->
            val breedId = backStackEntry.arguments?.getString("breedId") ?: ""
            BreedDetailScreen(breedId = breedId)
        }
    }
}