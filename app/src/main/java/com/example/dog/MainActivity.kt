package com.example.dog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.dog.theme.DogAppTheme
import com.example.dog.ui.components.BottomNavigation
import com.example.dog.ui.nav.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigation(navController = navController)
                    }
                ) {
                    paddingValues ->
                    Surface (modifier = Modifier.padding(paddingValues))
                    {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
