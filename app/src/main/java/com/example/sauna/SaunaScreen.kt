package com.example.sauna

import OrderSummaryScreen
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sauna.data.DataSource

import com.example.sauna.ui.OrderViewModel
import com.example.sauna.ui.SelectOptionScreen
import com.example.sauna.ui.StartOrderScreen

enum class SaunaScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Aika(title = R.string.kesto),
    Date(title = R.string.choose_saunavuoro_date),
    Summary(title = R.string.order_summary)
}

private fun confirmOrder(context: Context, onConfirmed: () -> Unit) {
    val alertDialog = AlertDialog.Builder(context)

    alertDialog.setTitle("Varmista maksu")
    alertDialog.setMessage("Haluatko varmasti maksaa tilauksen?")

    alertDialog.setPositiveButton("Kyllä") { _, _ ->
        onConfirmed()
    }
    alertDialog.setNegativeButton("Takaisin") { dialog, _ ->
        dialog.dismiss()
    }
    alertDialog.show()
}

@Composable
fun SaunaAppBar(
    currentScreen: SaunaScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun SaunaApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = SaunaScreen.valueOf(
        backStackEntry?.destination?.route ?: SaunaScreen.Start.name
    )

    Scaffold(
        topBar = {
            SaunaAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = SaunaScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SaunaScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.sauna_types,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(SaunaScreen.Aika.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = SaunaScreen.Aika.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(SaunaScreen.Date.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = DataSource.saunaDurationPrices.map { (resourceId, _) -> context.resources.getString(resourceId) },

                    onSelectionChanged = { viewModel.setAika(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = SaunaScreen.Date.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(SaunaScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = SaunaScreen.Summary.name) {
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        confirmOrder(context)

                        {
                            shareOrder(context, subject = subject, summary = summary)
                            navController.popBackStack(SaunaScreen.Start.name, inclusive = false)
                        }
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

        }
    }
}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(SaunaScreen.Start.name, inclusive = false)
}

private fun shareOrder(context: Context, subject: String, summary: String) {
    // Create an ACTION_SEND implicit intent with order details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_saunavuoro_order)
        )
    )
}

