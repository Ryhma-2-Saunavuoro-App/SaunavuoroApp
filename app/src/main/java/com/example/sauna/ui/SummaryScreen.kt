
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.sauna.R
import com.example.sauna.data.OrderUiState
import com.example.sauna.ui.components.FormattedPriceLabel
import com.example.sauna.data.DataSource

@Composable
fun OrderSummaryScreen(
    orderUiState: OrderUiState,
    onCancelButtonClicked: () -> Unit = {},
    onSendButtonClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dataSource = DataSource
    val context = LocalContext.current
    val time = orderUiState.aika
    val quantity = orderUiState.quantity
    val saunaResource = dataSource.sauna_types[quantity - 1].first
    val saunaType = context.getString(saunaResource)

    val orderSummary = stringResource(
        R.string.order_details,
        saunaType,
        time,
        orderUiState.date,
        orderUiState.price
    )
    val newOrder = stringResource(R.string.new_saunavuoro_order)

    val items = listOf(
        Pair(stringResource(R.string.sauna_type), saunaType),
        Pair(stringResource(R.string.duration), time),
        Pair(stringResource(R.string.order_date), orderUiState.date)
    )

    var cancelButtonClicked by remember { mutableStateOf(false) }

    DisposableEffect(cancelButtonClicked) {
        if (cancelButtonClicked) {
            onCancelButtonClicked()
            // Finish the activity
            (context as? OnBackPressedDispatcherOwner)?.onBackPressedDispatcher?.onBackPressed()
        }

        onDispose { /* Cleanup logic if needed */ }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            items.forEach { item ->
                Text(item.first.uppercase())
                Text(text = item.second, fontWeight = FontWeight.Bold)
                Divider(thickness = dimensionResource(R.dimen.thickness_divider))
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            FormattedPriceLabel(
                subtotal = orderUiState.price,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .weight(1f, false),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(modifier = Modifier.weight(1f), onClick = {
                cancelButtonClicked = true
            }) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onSendButtonClicked(newOrder, orderSummary) }
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}

@Preview
@Composable
fun OrderSummaryPreview() {
    OrderSummaryScreen(
        orderUiState = OrderUiState(0, "Test", "Test", "$300.00"),
        onSendButtonClicked = { subject: String, summary: String -> },
        onCancelButtonClicked = {},
        modifier = Modifier.fillMaxHeight()
    )
}
