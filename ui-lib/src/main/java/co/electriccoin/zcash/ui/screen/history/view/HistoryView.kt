package co.electriccoin.zcash.ui.screen.history.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.twotone.ArrowCircleDown
import androidx.compose.material.icons.twotone.ArrowCircleUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.history.HistoryTag
import co.electriccoin.zcash.ui.screen.history.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Preview("History")
@Composable
private fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            History(
                transactionState = TransactionHistorySyncState.Loading,
                goBack = {}
            )
        }
    }
}

val dateFormat: DateFormat by lazy {
    SimpleDateFormat.getDateTimeInstance(
        SimpleDateFormat.MEDIUM,
        SimpleDateFormat.SHORT,
        Locale.getDefault()
    )
}

@Composable
fun History(
    transactionState: TransactionHistorySyncState,
    goBack: () -> Unit
) {
    Scaffold(topBar = {
        HistoryTopBar(onBack = goBack)
    }) { paddingValues ->
        HistoryMainContent(
            transactionState = transactionState,
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        )
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

@Composable
private fun HistoryMainContent(
    transactionState: TransactionHistorySyncState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (transactionState) {
            is TransactionHistorySyncState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(alignment = Center)
                        .testTag(HistoryTag.PROGRESS)
                )
            }
            is TransactionHistorySyncState.Syncing -> {
                Column(
                    modifier = Modifier.align(alignment = TopCenter)
                ) {
                    Body(
                        text = stringResource(id = R.string.history_syncing),
                        modifier = Modifier
                            .padding(
                                top = ZcashTheme.dimens.spacingSmall,
                                bottom = ZcashTheme.dimens.spacingSmall,
                                start = ZcashTheme.dimens.spacingDefault,
                                end = ZcashTheme.dimens.spacingDefault
                            )
                    )
                    HistoryList(transactions = transactionState.transactions)
                }
                // Add progress indicator only in the state of empty transaction
                if (transactionState.hasNoTransactions()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(alignment = Center)
                            .testTag(HistoryTag.PROGRESS)

                    )
                }
            }
            is TransactionHistorySyncState.Done -> {
                if (transactionState.hasNoTransactions()) {
                    Body(
                        text = stringResource(id = R.string.history_empty),
                        modifier = Modifier
                            .padding(all = ZcashTheme.dimens.spacingDefault)
                            .align(alignment = Center)
                    )
                } else {
                    HistoryList(transactions = transactionState.transactions)
                }
            }
        }
    }
}

@Composable
private fun HistoryList(transactions: ImmutableList<TransactionOverview>) {
    val currency = ZcashCurrency.fromResources(LocalContext.current)
    LazyColumn(
        contentPadding = PaddingValues(all = ZcashTheme.dimens.spacingDefault),
        modifier = Modifier.testTag(HistoryTag.TRANSACTION_LIST)
    ) {
        items(transactions) {
            HistoryItem(
                transaction = it,
                currency = currency
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
fun HistoryItem(
    transaction: TransactionOverview,
    currency: ZcashCurrency
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ZcashTheme.dimens.spacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val transactionText: String
        val transactionIcon: ImageVector
        when (transaction.getExtendedState()) {
            TransactionExtendedState.SENT -> {
                transactionText = stringResource(id = R.string.history_item_sent)
                transactionIcon = Icons.TwoTone.ArrowCircleUp
            }
            TransactionExtendedState.SENDING -> {
                transactionText = stringResource(id = R.string.history_item_sending)
                transactionIcon = Icons.Outlined.ArrowCircleUp
            }
            TransactionExtendedState.RECEIVED -> {
                transactionText = stringResource(id = R.string.history_item_received)
                transactionIcon = Icons.TwoTone.ArrowCircleDown
            }
            TransactionExtendedState.RECEIVING -> {
                transactionText = stringResource(id = R.string.history_item_receiving)
                transactionIcon = Icons.Outlined.ArrowCircleDown
            }
            TransactionExtendedState.EXPIRED -> {
                transactionText = stringResource(id = R.string.history_item_expired)
                transactionIcon = Icons.Filled.Cancel
            }
        }

        Image(
            imageVector = transactionIcon,
            contentDescription = transactionText,
            modifier = Modifier.padding(all = ZcashTheme.dimens.spacingTiny)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Body(
                text = transactionText,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            val dateString = transaction.minedHeight?.let {
                transaction.blockTimeEpochSeconds?.let { blockTimeEpochSeconds ->
                    // * 1000 to covert to millis
                    @Suppress("MagicNumber")
                    dateFormat.format(blockTimeEpochSeconds.times(1000L))
                } ?: stringResource(id = R.string.history_item_date_not_available)
            } ?: stringResource(id = R.string.history_item_date_not_available)
            // For now, use the same label for the above missing transaction date

            Body(
                text = dateString,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column {
            Row(modifier = Modifier.align(alignment = Alignment.End)) {
                val zecString = if (transaction.isSentTransaction) {
                    "-${transaction.netValue.toZecString()}"
                } else {
                    transaction.netValue.toZecString()
                }
                Body(text = zecString)

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

                Body(text = currency.name)
            }
        }
    }
}

enum class TransactionExtendedState {
    SENT,
    SENDING,
    RECEIVED,
    RECEIVING,
    EXPIRED
}

private fun TransactionOverview.getExtendedState(): TransactionExtendedState {
    return when (transactionState) {
        TransactionState.Expired -> {
            TransactionExtendedState.EXPIRED
        }
        TransactionState.Confirmed -> {
            if (isSentTransaction) {
                TransactionExtendedState.SENT
            } else {
                TransactionExtendedState.RECEIVED
            }
        }
        TransactionState.Pending -> {
            if (isSentTransaction) {
                TransactionExtendedState.SENDING
            } else {
                TransactionExtendedState.RECEIVING
            }
        }
        else -> {
            error("Unexpected transaction state found while calculating its extended state.")
        }
    }
}
