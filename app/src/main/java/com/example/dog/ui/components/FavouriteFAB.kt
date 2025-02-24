package com.example.dog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FavouriteFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Favourite"
        )
    }
}