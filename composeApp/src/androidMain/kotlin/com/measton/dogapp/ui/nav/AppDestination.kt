package com.measton.dogapp.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.measton.dogapp.resources.Res
import com.measton.dogapp.resources.cat_paw
import com.measton.dogapp.resources.details_tab
import com.measton.dogapp.resources.dog_bone
import com.measton.dogapp.resources.favourites_tab
import com.measton.dogapp.resources.home_tab
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class AppDestination(
    val route: String,
    val iconRes: DrawableResource? = null,
    val titleResId: StringResource,
    val imageVector: ImageVector? = null
) {
    object Favourites: AppDestination("Favourites", Res.drawable.cat_paw, Res.string.favourites_tab)
    object Home: AppDestination("Home", Res.drawable.dog_bone, Res.string.home_tab)
    object BreedDetail: AppDestination("Breed/{breedId}", null, Res.string.details_tab, imageVector = Icons.Filled.Info,) {
        fun route(breedId: String) = "Breed/$breedId"
    }
}
