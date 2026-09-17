package com.measton.dogapp.ui.breeddetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.measton.dogapp.resources.Res
import com.measton.dogapp.resources.breed_details_format
import com.measton.dogapp.resources.info_failed_to_load
import com.measton.dogapp.resources.loading
import com.measton.dogapp.resources.refresh
import com.measton.dogapp.ui.components.FavouriteFAB
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BreedDetailScreen(
    breedId: String, viewModel: BreedDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(breedId) {
        viewModel.fetchBreedDetails(breedId)
    }

    Scaffold(
        floatingActionButton = {
            FavouriteFAB(onClick = {})
        })
    { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (val state = uiState) {
                BreedDetailUiState.Loading -> {
                    Text(
                        text = stringResource(Res.string.loading),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }

                BreedDetailUiState.Error -> {
                    Text(
                        text = stringResource(Res.string.info_failed_to_load),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchBreedDetails(breedId) }) {
                        Text(
                            stringResource(Res.string.refresh),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                is BreedDetailUiState.Success -> {
                    val breed = state.breed
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            Res.string.breed_details_format,
                            breed.name,
                            breed.lifeSpan,
                            breed.temperament
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchBreedDetails(breedId) }) {
                        Text(
                            stringResource(Res.string.refresh),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn {
                        items(state.images) { image ->
                            AsyncImage(
                                model = image.url,
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
        }
    }
}