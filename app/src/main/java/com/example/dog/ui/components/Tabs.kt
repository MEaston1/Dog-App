package com.example.dog.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.dog.R
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow

@Composable
fun AnimalTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
){
    TabRow(selectedTabIndex = selectedTabIndex) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            text = { Text(stringResource(R.string.dog_tab)) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.dog_bone),
                    contentDescription = stringResource(R.string.dog_tab)
                )
            }
        )

        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            text = { Text(stringResource(R.string.cat_tab)) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.cat_paw),
                    contentDescription = stringResource(R.string.cat_tab)
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimalTabsPreview() {
    MaterialTheme {
        AnimalTabs(
            selectedTabIndex = 0,
            onTabSelected = {}
        )
    }
}
