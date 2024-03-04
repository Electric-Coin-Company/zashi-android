package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.twotone.ArrowCircleDown
import androidx.compose.material.icons.twotone.ArrowCircleUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Tiny
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.account.HistoryTag
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
@Preview("History")
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            HistoryContainer(
                transactionState = TransactionHistorySyncState.Loading,
                onItemClick = {},
                onTransactionIdClick = {}
            )
        }
    }
}

@Composable
@Preview("History List")
private fun ComposableHistoryListPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            HistoryContainer(
                transactionState =
                    TransactionHistorySyncState.Syncing(
                        @Suppress("MagicNumber")
                        persistentListOf(
                            TransactionOverviewFixture.new(netValue = Zatoshi(100000000)),
                            TransactionOverviewFixture.new(netValue = Zatoshi(200000000)),
                            TransactionOverviewFixture.new(netValue = Zatoshi(300000000)),
                        )
                    ),
                onItemClick = {},
                onTransactionIdClick = {}
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
@Suppress("LongMethod")
fun HistoryContainer(
    transactionState: TransactionHistorySyncState,
    onItemClick: (TransactionOverview) -> Unit,
    onTransactionIdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .then(
                    Modifier
                        .fillMaxSize()
                        .background(ZcashTheme.colors.historyBackgroundColor)
                )
    ) {
        when (transactionState) {
            is TransactionHistorySyncState.Loading -> {
                CircularScreenProgressIndicator(
                    modifier =
                        Modifier
                            .align(alignment = Center)
                            .testTag(HistoryTag.PROGRESS)
                )
            }
            is TransactionHistorySyncState.Syncing -> {
                HistoryList(
                    transactions = transactionState.transactions,
                    onItemClick = onItemClick,
                    onTransactionIdClick = onTransactionIdClick
                )
            }
            is TransactionHistorySyncState.Done -> {
                HistoryList(
                    transactions = transactionState.transactions,
                    onItemClick = onItemClick,
                    onTransactionIdClick = onTransactionIdClick
                )
            }
        }
    }
}

@Composable
private fun HistoryList(
    transactions: ImmutableList<TransactionOverview>,
    onItemClick: (TransactionOverview) -> Unit,
    onTransactionIdClick: (String) -> Unit
) {
    val currency = ZcashCurrency.getLocalizedName(LocalContext.current)

    LazyColumn(
        modifier = Modifier.testTag(HistoryTag.TRANSACTION_LIST)
    ) {
        itemsIndexed(transactions) { _, item ->
            HistoryItem(
                transaction = item,
                currency = currency,
                onItemClick = onItemClick,
                onIdClick = onTransactionIdClick,
            )

            Divider(
                color = ZcashTheme.colors.dividerColor,
                thickness = DividerDefaults.Thickness,
                modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingDefault)
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
fun HistoryItem(
    transaction: TransactionOverview,
    currency: String,
    onItemClick: (TransactionOverview) -> Unit,
    onIdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val transactionTypeText: String
    val transactionTypeIcon: ImageVector
    when (transaction.getExtendedState()) {
        TransactionExtendedState.SENT -> {
            transactionTypeText = stringResource(id = R.string.history_item_sent)
            transactionTypeIcon = Icons.TwoTone.ArrowCircleUp
        }
        TransactionExtendedState.SENDING -> {
            transactionTypeText = stringResource(id = R.string.history_item_sending)
            transactionTypeIcon = Icons.Outlined.ArrowCircleUp
        }
        TransactionExtendedState.RECEIVED -> {
            transactionTypeText = stringResource(id = R.string.history_item_received)
            transactionTypeIcon = Icons.TwoTone.ArrowCircleDown
        }
        TransactionExtendedState.RECEIVING -> {
            transactionTypeText = stringResource(id = R.string.history_item_receiving)
            transactionTypeIcon = Icons.Outlined.ArrowCircleDown
        }
        TransactionExtendedState.EXPIRED -> {
            transactionTypeText = stringResource(id = R.string.history_item_expired)
            transactionTypeIcon = Icons.Filled.Cancel
        }
    }

    Row(
        modifier =
            modifier
                .then(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(transaction) }
                        .background(color = ZcashTheme.colors.historyBackgroundColor)
                        .padding(all = ZcashTheme.dimens.spacingDefault)
                ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = transactionTypeIcon,
            contentDescription = transactionTypeText,
            modifier = Modifier.padding(all = ZcashTheme.dimens.spacingTiny)
        )
        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Body(
                        text = transactionTypeText,
                        color = Color.Black,
                        modifier = Modifier.testTag(HistoryTag.TRANSACTION_ITEM)
                    )

                    val dateString =
                        transaction.minedHeight?.let {
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
                        val zecString =
                            if (transaction.isSentTransaction) {
                                "-${transaction.netValue.toZecString()}"
                            } else {
                                transaction.netValue.toZecString()
                            }
                        Body(text = zecString)

                        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

                        Body(text = currency)
                    }
                }
            }
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            val txId = transaction.txIdString()
            Tiny(
                text = txId,
                modifier =
                    Modifier
                        .clickable { onIdClick(txId) }
                        .testTag(HistoryTag.TRANSACTION_ID)
            )
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
