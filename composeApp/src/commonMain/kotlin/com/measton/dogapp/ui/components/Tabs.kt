package com.measton.dogapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.measton.dogapp.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import com.measton.dogapp.resources.cat_paw
import com.measton.dogapp.resources.cat_tab
import com.measton.dogapp.resources.dog_bone
import com.measton.dogapp.resources.dog_tab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AnimalTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
){
    TabRow(selectedTabIndex = selectedTabIndex) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },
            text = { Text(stringResource(Res.string.dog_tab)) },
            icon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.dog_bone),
                    contentDescription = stringResource(Res.string.dog_tab)
                )
            }
        )

        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            text = { Text(stringResource(Res.string.cat_tab)) },
            icon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.cat_paw),
                    contentDescription = stringResource(Res.string.cat_tab)
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
