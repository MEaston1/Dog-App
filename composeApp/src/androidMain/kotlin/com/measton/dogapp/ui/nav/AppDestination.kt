package com.measton.dogapp.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.measton.dogapp.R
import com.measton.dogapp.resources.Res
import com.measton.dogapp.resources.details_tab
import com.measton.dogapp.resources.favourites_tab
import com.measton.dogapp.resources.home_tab
import org.jetbrains.compose.resources.StringResource

sealed class AppDestination(
    val route: String,
    @DrawableRes val iconResId: Int? = null,
    val titleResId: StringResource,
    val imageVector: ImageVector? = null
) {
    object Favourites: AppDestination("Favourites", R.drawable.cat_paw, Res.string.favourites_tab)
    object Home: AppDestination("Home", R.drawable.dog_bone, Res.string.home_tab)
    object BreedDetail: AppDestination("Breed/{breedId}", null, Res.string.details_tab, imageVector = Icons.Filled.Info,) {
        fun route(breedId: String) = "Breed/$breedId"
    }
}