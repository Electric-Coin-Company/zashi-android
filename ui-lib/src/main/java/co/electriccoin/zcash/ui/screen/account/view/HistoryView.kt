package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.Tiny
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.account.HistoryTag
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.screen.account.state.TransactionOverviewExt
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
                            TransactionOverviewExt(
                                TransactionOverviewFixture.new(netValue = Zatoshi(100000000)),
                                null
                            ),
                            TransactionOverviewExt(
                                TransactionOverviewFixture.new(netValue = Zatoshi(200000000)),
                                null
                            ),
                            TransactionOverviewExt(
                                TransactionOverviewFixture.new(netValue = Zatoshi(300000000)),
                                null
                            ),
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
        // TODO [#1171]: Remove default MonetarySeparators locale
        // TODO [#1171]: https://github.com/Electric-Coin-Company/zashi-android/issues/1171
        Locale.US
    )
}

@Composable
@Suppress("LongMethod")
fun HistoryContainer(
    transactionState: TransactionHistorySyncState,
    onItemClick: (TransactionOverviewExt) -> Unit,
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
    transactions: ImmutableList<TransactionOverviewExt>,
    onItemClick: (TransactionOverviewExt) -> Unit,
    onTransactionIdClick: (String) -> Unit
) {
    if (transactions.isEmpty()) {
        Column {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.account_history_empty),
                style = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular,
                color = ZcashTheme.colors.textCommon,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.testTag(HistoryTag.TRANSACTION_LIST)
        ) {
            itemsIndexed(transactions) { _, item ->
                HistoryItem(
                    transaction = item,
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
}

private enum class ItemExpandedState {
    COLLAPSED,
    EXPANDED,
    EXPANDED_ADDRESS,
    EXPANDED_ID
}

const val ADDRESS_IN_TITLE_WIDTH_RATIO = 0.5f

@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
fun HistoryItem(
    transaction: TransactionOverviewExt,
    onItemClick: (TransactionOverviewExt) -> Unit,
    onIdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeText: String
    val textColor: Color
    val typeIcon: ImageVector
    val textStyle: TextStyle
    when (transaction.overview.getExtendedState()) {
        TransactionExtendedState.SENT -> {
            typeText = stringResource(id = R.string.account_history_item_sent)
            typeIcon = ImageVector.vectorResource(R.drawable.trx_send_icon)
            textColor = MaterialTheme.colorScheme.onBackground
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular
        }
        TransactionExtendedState.SENDING -> {
            typeText = stringResource(id = R.string.account_history_item_sending)
            typeIcon = ImageVector.vectorResource(R.drawable.trx_send_icon)
            textColor = ZcashTheme.colors.textDescription
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRunning
        }
        TransactionExtendedState.SEND_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_send_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.trx_send_icon)
            textColor = ZcashTheme.colors.dangerous
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleFailed
        }

        TransactionExtendedState.RECEIVED -> {
            typeText = stringResource(id = R.string.account_history_item_received)
            typeIcon = ImageVector.vectorResource(R.drawable.trx_receive_icon)
            textColor = MaterialTheme.colorScheme.onBackground
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular
        }
        TransactionExtendedState.RECEIVING -> {
            typeText = stringResource(id = R.string.account_history_item_receiving)
            typeIcon = ImageVector.vectorResource(R.drawable.trx_receive_icon)
            textColor = ZcashTheme.colors.textDescription
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRunning
        }
        TransactionExtendedState.RECEIVE_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_receive_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.trx_receive_icon)
            textColor = ZcashTheme.colors.dangerous
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleFailed
        }
    }

    var expandedState: ItemExpandedState by rememberSaveable {
        mutableStateOf(ItemExpandedState.COLLAPSED)
    }

    Row(
        modifier =
            modifier
                .then(
                    Modifier
                        .background(color = ZcashTheme.colors.historyBackgroundColor)
                        .clickable {
                            if (expandedState == ItemExpandedState.COLLAPSED) {
                                expandedState = ItemExpandedState.EXPANDED
                            }
                            onItemClick(transaction)
                        }
                        .padding(all = ZcashTheme.dimens.spacingLarge)
                        .animateContentSize()
                )
    ) {
        Image(
            imageVector = typeIcon,
            contentDescription = typeText,
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = typeText,
                    style = textStyle,
                    color = textColor,
                    modifier = Modifier.testTag(HistoryTag.TRANSACTION_ITEM_TITLE)
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                if (transaction.recipient != null && transaction.recipient is TransactionRecipient.Address) {
                    Text(
                        text = transaction.recipient.addressValue,
                        style = ZcashTheme.extendedTypography.transactionItemStyles.addressCollapsed,
                        color = ZcashTheme.colors.textDescription,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier =
                            Modifier
                                .fillMaxWidth(ADDRESS_IN_TITLE_WIDTH_RATIO)
                    )
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.trx_shielded),
                        contentDescription = stringResource(id = R.string.account_history_item_shielded)
                    )
                }

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                Spacer(modifier = Modifier.weight(1f))

                StyledBalance(
                    balanceString = transaction.overview.netValue.toZecString(),
                    textStyles =
                        Pair(
                            first = ZcashTheme.extendedTypography.transactionItemStyles.valueFirstPart,
                            second = ZcashTheme.extendedTypography.transactionItemStyles.valueSecondPart
                        ),
                    textColor =
                        if (transaction.overview.isSentTransaction) {
                            ZcashTheme.colors.historySendColor
                        } else {
                            ZcashTheme.colors.textCommon
                        },
                    prefix =
                        if (transaction.overview.isSentTransaction) {
                            stringResource(id = R.string.account_history_item_sent_prefix)
                        } else {
                            stringResource(id = R.string.account_history_item_received_prefix)
                        }
                )
            }

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            val dateString =
                transaction.overview.minedHeight?.let {
                    transaction.overview.blockTimeEpochSeconds?.let { blockTimeEpochSeconds ->
                        // * 1000 to covert to millis
                        @Suppress("MagicNumber")
                        dateFormat.format(blockTimeEpochSeconds.times(1000))
                    } ?: ""
                } ?: ""

            Text(
                text = dateString,
                style = ZcashTheme.extendedTypography.transactionItemStyles.date,
                color = ZcashTheme.colors.textDescription,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (expandedState >= ItemExpandedState.EXPANDED) {
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                val txId = transaction.overview.txIdString()
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
}

enum class TransactionExtendedState {
    SENT,
    SENDING,
    SEND_FAILED,
    RECEIVED,
    RECEIVING,
    RECEIVE_FAILED,
}

private fun TransactionOverview.getExtendedState(): TransactionExtendedState {
    return when (transactionState) {
        TransactionState.Expired -> {
            if (isSentTransaction) {
                TransactionExtendedState.SEND_FAILED
            } else {
                TransactionExtendedState.RECEIVE_FAILED
            }
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
