package com.example.dog.ui.components

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.dog.ui.nav.AppDestination
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dog.ui.SharedPetViewModel
import kotlinx.coroutines.launch

@Composable
fun BottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val sharedPetViewModel: SharedPetViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val currentBreedId by sharedPetViewModel.currentBreedId.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
                        coroutineScope.launch {
                            Log.d("BottomNavigation", "BreedId at click: $currentBreedId")
                            if(currentBreedId != null){
                                navController.navigate("breed/$currentBreedId") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                // Optional: Handle case when no breed is selected
                                Log.d("BottomNavigation", "No breed selected yet")
                            }
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
