package com.measton.dogapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.measton.dogapp.ui.components.BottomNavigation
import com.measton.dogapp.ui.nav.NavGraph
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App { currentBreedId, onBreedViewed ->
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigation(navController = navController, currentBreedId = currentBreedId)
                    }
                ) { paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues)) {
                        NavGraph(navController = navController, onBreedViewed = onBreedViewed)
                    }
                }
            }
        }
    }
}
