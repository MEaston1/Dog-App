package com.example.dog.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

// I added the androidx hiltViewModel import as I figure if I am going to annotate DogViewModel and several areas of the app with Hilt annotations I may as well utilise
// it within my composable functions as well, hitViewModel cannot be imported through an annotation on composable functions.
@Composable
fun DogImageScreen(navController: NavHostController, viewModel: DogViewModel = hiltViewModel()) {
    val dogImage = viewModel.dogImage.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect (viewModel) {
        viewModel.fetchRandomDogImage()
    }
    Log.d("DogImageScreen", "The dogImage is: $dogImage")
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        dogImage?.let {
            Image(
                painter = rememberAsyncImagePainter(it.url),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .testTag("dogImage"),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("breed/${it.breeds?.firstOrNull()?.id}") }) {
                Text("Breed Details")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.fetchRandomDogImage()
                }
            },
            modifier = Modifier.testTag("fetchDogButton")
        ) {
            Text("Fetch Another Dog Image")
        }
    }
}