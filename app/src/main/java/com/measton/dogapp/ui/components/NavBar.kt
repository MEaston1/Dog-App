package com.measton.dogapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.measton.dogapp.ui.nav.AppDestination
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(navController: NavHostController, currentBreedId: String?) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        val items = listOf(
            AppDestination.Favourites,
            AppDestination.Home,
            AppDestination.BreedDetail
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.imageVector != null) {
                        Icon(imageVector = item.imageVector, contentDescription = null)
                    } else {
                        Icon(painter = painterResource(id = item.iconResId!!), contentDescription = null)
                    }
                },
                label = { Text(stringResource(id = item.titleResId)) },
                selected = currentRoute == item.route,
                onClick = {
                    if(item == AppDestination.BreedDetail) {
                        if(currentBreedId != null){
                            navController.navigate(AppDestination.BreedDetail.route(currentBreedId)) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            // Optional: Handle case when no breed is selected
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
