package com.example.dog.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.dog.HomeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DogImageScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<HomeActivity>()

    @Test
    fun testImageAppearsOnButtonClick() {

        composeTestRule.onNodeWithTag("fetchDogImageButton").performClick()


        composeTestRule.onNodeWithTag("dogImage").assertIsDisplayed()
    }
}