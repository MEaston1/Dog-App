package com.measton.dogapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.measton.dogapp.ui.breeddetail.BreedDetailScreen
import com.measton.dogapp.ui.dogImage.DogImageScreen

@Composable
fun NavGraph(navController: NavHostController, onBreedViewed: (String?) -> Unit) {
    NavHost(navController, startDestination = AppDestination.Home.route) {
        composable(route = AppDestination.Home.route) {
            DogImageScreen(navController = navController, onBreedViewed = onBreedViewed)
        }
        composable(route = AppDestination.Favourites.route) {
            //FavouritesScreen(navController = navController)
        }
        composable(
            route = AppDestination.BreedDetail.route,
            arguments = listOf(navArgument("breedId") { type = NavType.StringType })
        ) { backStackEntry ->
            val breedId = backStackEntry.arguments?.getString("breedId") ?: ""
            BreedDetailScreen(breedId = breedId)
        }
    }
}