package com.example.dog.ui.nav

import com.example.dog.R

sealed class AppDestination(
    val routeResId: String,
    val iconResId: Int,
    val titleResId: Int
) {
    object Favourites: AppDestination("Favourites", R.drawable.cat_paw, R.string.favourites_tab)
    object Home: AppDestination("Home", R.drawable.dog_bone, R.string.home_tab)
    object Details: AppDestination("Details", R.drawable.cat_paw, R.string.details_tab)
}