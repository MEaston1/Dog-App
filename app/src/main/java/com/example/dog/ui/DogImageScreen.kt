package com.example.dog.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dog.ui.components.FavouriteFAB
import kotlinx.coroutines.launch
import com.example.dog.R

@Composable
fun DogImageScreen(navController: NavHostController, viewModel: DogViewModel = hiltViewModel()) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val dogImage = viewModel.dogImage.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val hasInitialFetch = remember { mutableStateOf(false) }
    LaunchedEffect(viewModel) {
        if (!hasInitialFetch.value) {
            Log.d("fetching initial dog image", hasInitialFetch.value.toString())
            viewModel.fetchRandomDogImage()
            hasInitialFetch.value = true
        }
    }

    Scaffold(
        floatingActionButton = {
            FavouriteFAB(onClick = {})
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
    { paddingValues ->
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                dogImage?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it.url),
                        contentDescription = null,
                        modifier = Modifier
                            .size(250.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .testTag("dogImage"),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ){
                            Button(
                                modifier = Modifier
                                    .testTag("breedDetailButton")
                                    .size(250.dp, 50.dp),
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                onClick = { navController.navigate("breed/${it.breeds?.firstOrNull()?.id}") }) {
                                Text(
                                    text = stringResource(id = R.string.pet_details),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.fetchRandomDogImage()
                                    }
                                },
                                modifier = Modifier
                                    .testTag("fetchDogButton")
                            ) {
                                Text(
                                    text = stringResource(id = R.string.fetch_pet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                dogImage?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it.url),
                        contentDescription = null,
                        modifier = Modifier
                            .size(300.dp)
                            .testTag("dogImage")
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(100.dp))
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        )
                    ){
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ){
                            Button(
                                modifier = Modifier
                                    .testTag("breedDetailButton")
                                    .size(250.dp, 50.dp),
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                onClick = { navController.navigate("breed/${it.breeds?.firstOrNull()?.id}") }) {
                                Text(
                                    text = stringResource(id = R.string.pet_details),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.fetchRandomDogImage()
                                    }
                                },
                                modifier = Modifier
                                    .testTag("fetchDogButton")
                            ) {
                                Text(
                                    text = stringResource(id = R.string.fetch_pet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}