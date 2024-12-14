package com.example.dog.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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

@Composable
fun DogImageScreen(navController: NavHostController, viewModel: DogViewModel = hiltViewModel()) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val dogImage = viewModel.dogImage.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect (viewModel) {
        viewModel.fetchRandomDogImage()
    }
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ){
            dogImage?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.url),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .testTag("dogImage"),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = { navController.navigate("breed/${it.breeds?.firstOrNull()?.id}") }) {
                        Text("Breed Details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)
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
                        Text("Fetch Another Dog Image",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    } else {
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
                    Text("Breed Details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
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
                Text("Fetch Another Dog Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}