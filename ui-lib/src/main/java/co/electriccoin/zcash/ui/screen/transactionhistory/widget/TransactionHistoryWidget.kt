package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.Transaction

@Composable
fun TransactionHistoryWidget(state: TransactionHistoryWidgetState) {
    when (state) {
        is TransactionHistoryWidgetState.Data -> Data(state)
        is TransactionHistoryWidgetState.Empty -> Empty(state)
    }
}

@Composable
private fun Data(state: TransactionHistoryWidgetState.Data) {
    Column {
        TransactionHistoryWidgetHeader(
            state = state.header,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(8.dp))
        state.transactions.forEachIndexed { index, item ->
            Transaction(
                state = item,
                modifier = Modifier.padding(horizontal = 4.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp)
            )

            if (index != state.transactions.lastIndex) {
                ZashiHorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Empty(state: TransactionHistoryWidgetState.Empty) {
    Box {
        Column {
            Spacer(Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.transaction_widget_loading_background),
                contentDescription = null
            )
            Spacer(Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.transaction_widget_loading_background),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        .41f to ZashiColors.Surfaces.bgPrimary,
                        1f to ZashiColors.Surfaces.bgPrimary,
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(90.dp))
            Image(
                painter = painterResource(R.drawable.ic_transaction_widget_empty),
                contentDescription = null,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "There’s nothing here, yet."
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Make the first move..."
            )
            Spacer(Modifier.height(20.dp))
            ZashiButton(
                state = state.sendTransaction,
                colors = ZashiButtonDefaults.tertiaryColors(),
            )
        }
    }
}

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    BlankSurface {
        TransactionHistoryWidget(
            state = TransactionHistoryWidgetStateFixture.new()
        )
    }
}

@PreviewScreens
@Composable
private fun EmptyPreview() = ZcashTheme {
    BlankSurface {
        TransactionHistoryWidget(
            state = TransactionHistoryWidgetState.Empty(
                sendTransaction = ButtonState(
                    text = stringRes("Send a transaction"),
                    onClick = {}
                )
            )
        )
    }
}