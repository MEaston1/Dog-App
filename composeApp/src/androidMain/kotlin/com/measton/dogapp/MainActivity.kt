package com.measton.dogapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.measton.dogapp.theme.DogAppTheme
import com.measton.dogapp.ui.components.BottomNavigation
import com.measton.dogapp.ui.nav.NavGraph
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.measton.dogapp.ui.SharedPetViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogAppTheme {
                val sharedPetViewModel: SharedPetViewModel = koinViewModel()
                val currentBreedId by sharedPetViewModel.currentBreedId.collectAsState()
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigation(navController = navController, currentBreedId = currentBreedId)
                    }
                ) {
                    paddingValues ->
                    Surface (modifier = Modifier.padding(paddingValues))
                    {
                        NavGraph(navController = navController,
                            onBreedViewed = sharedPetViewModel::updateCurrentBreedId)
                    }
                }
            }
        }
    }
}
