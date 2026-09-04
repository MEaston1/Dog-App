package com.measton.dogapp.ui.dogImage

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.measton.dogapp.ui.components.FavouriteFAB
import kotlinx.coroutines.launch
import com.measton.dogapp.R
import com.measton.dogapp.ui.DogImageUiState
import com.measton.dogapp.ui.DogViewModel
import com.measton.dogapp.ui.components.AnimalTabs
import com.measton.dogapp.ui.nav.AppDestination
import org.koin.androidx.compose.koinViewModel

@Composable
fun DogImageScreen(navController: NavHostController, dogViewModel: DogViewModel = koinViewModel(), onBreedViewed: (String?) -> Unit) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val uiState = dogViewModel.uiState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val hasInitialFetch = remember { mutableStateOf(false) }
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    val dog = (uiState as? DogImageUiState.Success)?.dog
    LaunchedEffect(dog) {
        dog?.breeds?.firstOrNull()?.id?.let { breedId ->
            onBreedViewed(breedId)
        }
    }

    LaunchedEffect(dogViewModel) {
        if (!hasInitialFetch.value) {
            dogViewModel.fetchRandomDogImage()
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
        Column(modifier = Modifier.fillMaxSize()) {
            AnimalTabs(
                selectedTabIndex = selectedTabIndex.intValue,
                onTabSelected = { index -> selectedTabIndex.intValue = index }
            )
            when (uiState) {
                DogImageUiState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                DogImageUiState.Error -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.errordog),
                        contentDescription = null,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.info_failed_to_load),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        onClick = { dogViewModel.fetchRandomDogImage() },
                        modifier = Modifier.testTag("fetchDogButton")
                    ) {
                        Text(
                            text = stringResource(id = R.string.fetch_pet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                is DogImageUiState.Success -> {
                    val successDog = uiState.dog
                    val contentModifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                    val breedId = successDog.breeds.firstOrNull()?.id
                    val onDetails: () -> Unit =
                        { breedId?.let { navController.navigate(AppDestination.BreedDetail.route(it)) } }
                    val onFetch: () -> Unit =
                        { coroutineScope.launch { dogViewModel.fetchRandomDogImage() } }

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Row(
                            modifier = contentModifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DogImageView(url = successDog.url, size = 250.dp)
                            Spacer(modifier = Modifier.width(50.dp))
                            DogActionCard(onDetails = onDetails, onFetch = onFetch, detailsEnabled = breedId != null)
                        }
                    } else {
                        Column(
                            modifier = contentModifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            DogImageView(url = successDog.url, size = 300.dp)
                            Spacer(modifier = Modifier.height(100.dp))
                            DogActionCard(onDetails = onDetails, onFetch = onFetch, detailsEnabled = breedId != null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DogImageView(url: String, size: Dp) {
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(MaterialTheme.shapes.medium)
            .testTag("dogImage"),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun DogActionCard(
    onDetails: () -> Unit,
    onFetch: () -> Unit,
    detailsEnabled: Boolean = true,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                modifier = Modifier
                    .testTag("breedDetailButton")
                    .size(250.dp, 50.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = detailsEnabled,
                onClick = onDetails
            ) {
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
                onClick = onFetch,
                modifier = Modifier.testTag("fetchDogButton")
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