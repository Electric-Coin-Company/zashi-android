package co.electriccoin.zcash.ui.history.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Preview("History")
@Composable
private fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            History(
                transactions = persistentListOf(),
                isSyncing = true,
                goBack = {}
            )
        }
    }
}

@Composable
fun History(
    transactions: ImmutableList<TransactionOverview>,
    isSyncing: Boolean,
    goBack: () -> Unit
) {
    Scaffold(topBar = {
        HistoryTopBar(onBack = goBack)
    }) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (isSyncing) {
                Text(text = stringResource(id = R.string.history_syncing))
            }

            if (transactions.isEmpty()) {
                Text(text = stringResource(id = R.string.history_empty))
            } else {
                LazyColumn {
                    items(transactions) {
                        TransactionHistoryItem(it)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryItem(transaction: TransactionOverview) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.ic_dollar_currency_symbol),
            contentDescription = ""
        )
        Column {
            if (transaction.isSentTransaction) {
                Body(text = stringResource(id = R.string.history_sent))
            } else {
                Body(text = stringResource(id = R.string.history_received))
            }
        }
        Spacer(modifier = Modifier.weight(MINIMAL_WEIGHT))
        Column(horizontalAlignment = Alignment.End) {
            Body(text = transaction.netValue.toZecString())
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HistoryTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.history_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.history_back_content_description)
                )
            }
        }
    )
}
