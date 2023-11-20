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

import OrderSummaryScreen
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sauna.R
import com.example.sauna.data.DataSource
import com.example.sauna.data.OrderUiState

import com.example.sauna.ui.SelectOptionScreen
import com.example.sauna.ui.StartOrderScreen
import org.junit.Rule
import org.junit.Test

class SaunaOrderScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeOrderUiState = OrderUiState(
        quantity = 6,
        aika = "15 min",
        date = "Wed Jul 21",
        price = "$100",
        pickupOptions = listOf()
    )

    @Test
    fun startOrderScreen_verifyContent() {

        // When StartOrderScreen is loaded
        composeTestRule.setContent {
            StartOrderScreen(
                quantityOptions = DataSource.sauna_types,
                onNextButtonClicked = {}
            )
        }

        // Then all the options are displayed on the screen.
        DataSource.sauna_types.forEach {
            composeTestRule.onNodeWithStringId(it.first).assertIsDisplayed()
        }
    }


    @Test
    fun selectOptionScreen_verifyContent() {
        // Given list of options
        val aika = listOf("15min", "30min", "1h", "2h", "3h")
        // And subtotal
        val subtotal = "$100"

        // When SelectOptionScreen is loaded
        composeTestRule.setContent {
            SelectOptionScreen(subtotal = subtotal, options = aika)
        }

        // Then all the options are displayed on the screen.
        aika.forEach { aika ->
            composeTestRule.onNodeWithText(aika).assertIsDisplayed()
        }

        // And then the subtotal is displayed correctly.
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.subtotal_price,
                subtotal
            )
        ).assertIsDisplayed()

        // And then the next button is disabled
        composeTestRule.onNodeWithStringId(R.string.next).assertIsNotEnabled()
    }

    @Test
    fun selectOptionScreen_optionSelected_NextButtonEnabled() {
        // Given list of options
        val aika = listOf("15min", "30min", "1h", "2h", "3h")
        // And sub total
        val subTotal = "$100"

        // When SelectOptionScreen is loaded
        composeTestRule.setContent {
            SelectOptionScreen(subtotal = subTotal, options = aika)
        }

        // And one option is selected
        composeTestRule.onNodeWithText("15min").performClick()

        // Then the next button is disabled
        composeTestRule.onNodeWithStringId(R.string.next).assertIsEnabled()
    }

    @Test
    fun summaryScreen_verifyContentDisplay() {
        // When Summary Screen is loaded
        composeTestRule.setContent {
            OrderSummaryScreen(
                orderUiState = fakeOrderUiState,
                onCancelButtonClicked = {},
                onSendButtonClicked = { _, _ -> },
            )
        }

        // Then the UI is updated correctly.
        composeTestRule.onNodeWithText(fakeOrderUiState.aika).assertIsDisplayed()
        composeTestRule.onNodeWithText(fakeOrderUiState.date).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.subtotal_price,
                fakeOrderUiState.price
            )
        ).assertIsDisplayed()
    }
}
