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
package com.example.sauna.ui

import androidx.lifecycle.ViewModel
import com.example.sauna.R
import com.example.sauna.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.sauna.data.DataSource


private const val PRICE_FOR_SAME_DAY_ORDER = 10.00

class OrderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    fun setQuantity(numberofSaunavuoros: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberofSaunavuoros,
                price = calculatePrice(quantity = numberofSaunavuoros)
            )
        }
    }

    fun setAika(desiredAika: String) {
        _uiState.update { currentState ->
            val newPrice = calculatePrice(
                quantity = currentState.quantity,
                aika = desiredAika,
                pickupDate = currentState.date // Use the current state's date
            )
            currentState.copy(aika = desiredAika, price = newPrice)
        }
    }

    fun setDate(orderDate: String) {
        _uiState.update { currentState ->
            val newPrice = calculatePrice(
                quantity = currentState.quantity,
                aika = currentState.aika,
                pickupDate = orderDate // Pass the new orderDate here
            )
            currentState.copy(
                date = orderDate,
                price = newPrice
            )
        }
    }

    private fun getDurationResourceId(aika: String): Int {
        return when (aika) {
            "15 min" -> R.string.fifteen_min
            "30 min" -> R.string.thirty_min
            "1h" -> R.string.one_hour
            "2h" -> R.string.two_hours
            "3h" -> R.string.three_hours
            else -> 0 // Or some default value or throw an error
        }
    }

    fun resetOrder() {
        _uiState.value = OrderUiState(pickupOptions = pickupOptions())
    }


    private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        aika: String = _uiState.value.aika,
        pickupDate: String = _uiState.value.date // Added pickupDate parameter
    ): String {
        val resourceId = getDurationResourceId(aika)
        val aikaPrice = DataSource.saunaDurationPrices[resourceId] ?: 0.0
        var calculatedPrice = quantity * aikaPrice

        // Add an additional fee if the booking is for the same day
        if (pickupOptions()[0] == pickupDate) {
            calculatedPrice += PRICE_FOR_SAME_DAY_ORDER
        }

        val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
        return formattedPrice
    }


    private fun pickupOptions(): List<String> {
        val dateOptions = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }
}
