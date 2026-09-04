package com.measton.dogapp.ui.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.measton.dogapp.R

sealed class AppDestination(
    val route: String,
    @DrawableRes val iconResId: Int? = null,
    @StringRes val titleResId: Int,
    val imageVector: ImageVector? = null
) {
    object Favourites: AppDestination("Favourites", R.drawable.cat_paw, R.string.favourites_tab)
    object Home: AppDestination("Home", R.drawable.dog_bone, R.string.home_tab)
    object BreedDetail: AppDestination("Breed/{breedId}", null, R.string.details_tab, imageVector = Icons.Filled.Info,) {
        fun route(breedId: String) = "Breed/$breedId"
    }
}