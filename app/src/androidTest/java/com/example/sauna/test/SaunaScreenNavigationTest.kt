/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sauna.test

import android.icu.util.Calendar
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.sauna.SaunaApp
import com.example.sauna.SaunaScreen
import com.example.sauna.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class SaunaScreenNavigationTest {



    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupSaunaNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            SaunaApp(navController = navController)
        }
    }


    @Test
    fun saunaNavHost_verifyStartDestination() {
        navController.assertCurrentRouteName(SaunaScreen.Start.name)
    }

    @Test
    fun saunaNavHost_verifyBackNavigationNotShownOnStartOrderScreen() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).assertDoesNotExist()
    }

    @Test
    fun saunaNavHost_clickOneCupcake_navigatesToSelectAikaScreen() {
        composeTestRule.onNodeWithStringId(R.string.ir_sauna)
            .performClick()
        navController.assertCurrentRouteName(SaunaScreen.Aika.name)
    }


    @Test
    fun saunaNavHost_clickNextOnAikaScreen_navigatesToPickupScreen() {
        navigateToAikaScreen()
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick()
        navController.assertCurrentRouteName(SaunaScreen.Date.name)
    }

    @Test
    fun saunaNavHost_clickBackOnAikaScreen_navigatesToStartOrderScreen() {
        navigateToAikaScreen()
        performNavigateUp()
        navController.assertCurrentRouteName(SaunaScreen.Start.name)
    }

    @Test
    fun saunaNavHost_clickCancelOnAikaScreen_navigatesToStartOrderScreen() {
        navigateToAikaScreen()
        composeTestRule.onNodeWithStringId(R.string.cancel)
            .performClick()
        navController.assertCurrentRouteName(SaunaScreen.Start.name)
    }

    @Test
    fun saunaNavHost_clickNextOnPickupScreen_navigatesToSummaryScreen() {
        navigateToPickupScreen()
        composeTestRule.onNodeWithText(getFormattedDate())
            .performClick()
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick()
        navController.assertCurrentRouteName(SaunaScreen.Summary.name)
    }

    @Test
    fun saunaNavHost_clickBackOnPickupScreen_navigatesToAikaScreen() {
        navigateToPickupScreen()
        performNavigateUp()
        navController.assertCurrentRouteName(SaunaScreen.Aika.name)
    }

    @Test
    fun saunaNavHost_clickCancelOnPickupScreen_navigatesToStartOrderScreen() {
        navigateToPickupScreen()
        composeTestRule.onNodeWithStringId(R.string.cancel)
            .performClick()
        navController.assertCurrentRouteName(SaunaScreen.Start.name)
    }

    @Test
    fun saunaNavHost_clickCancelOnSummaryScreen_navigatesToStartOrderScreen() {
        navigateToSummaryScreen()
        composeTestRule.onNodeWithStringId(R.string.cancel)
            .performClick()
        navController.assertCurrentRouteName(SaunaScreen.Start.name)
    }

    private fun navigateToAikaScreen() {
        composeTestRule.onNodeWithStringId(R.string.ir_sauna)
            .performClick()
        composeTestRule.onNodeWithStringId(R.string.thirty_min)
            .performClick()
    }

    private fun navigateToPickupScreen() {
        navigateToAikaScreen()
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick()
    }

    private fun navigateToSummaryScreen() {
        navigateToPickupScreen()
        composeTestRule.onNodeWithText(getFormattedDate())
            .performClick()
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick()
    }

    private fun performNavigateUp() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).performClick()
    }

    private fun getFormattedDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(java.util.Calendar.DATE, 1)
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        return formatter.format(calendar.time)
    }
}
