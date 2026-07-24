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
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.measton.dogapp.ui.SharedPetViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogAppTheme {
                val sharedPetViewModel: SharedPetViewModel = viewModel()
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
