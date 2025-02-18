package com.example.dog.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter


@Composable
fun BreedDetailScreen(
    breedId: String,
) {
    val viewModel: BreedDetailViewModel = hiltViewModel()
    val breedDetails by viewModel.breedDetails.collectAsState()
    val breedImages by viewModel.breedImages.collectAsState()
    LaunchedEffect (breedId) {
        viewModel.fetchBreedDetails(breedId)
    }

    Column( modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = breedDetails?.name ?: "Loading...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        /*HorizontalMultiBrowseCarousel(
            state = (breedImages ?: emptyList()),
            modifier = Modifier.width(412.dp).height(221.dp),
            itemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
        ){

        }*/

        // Dog API resource provides: breed Name, Images, bred for, breed group, lifespan, temperament
        // Features missing from DogAPI: description, wiki url,
        Text(
            text = breedDetails?.let {
                "${it.name} have an average lifespan of ${it.life_span} years. Their temperament can be:  ${it.temperament}"
            } ?: "Info failed to load",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.fetchBreedDetails(breedId) }) {
            Text(text = "Refresh Images",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(breedImages ?: emptyList()) { image ->
                Image(
                    painter = rememberAsyncImagePainter(image.url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}