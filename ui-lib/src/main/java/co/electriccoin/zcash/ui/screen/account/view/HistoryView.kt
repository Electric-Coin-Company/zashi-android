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
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.CircularMidProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.Tiny
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.account.HistoryTag
import co.electriccoin.zcash.ui.screen.account.model.HistoryItemExpandableState
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
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
                transactionState = TransactionUiState.Loading,
                onTransactionItemAction = {}
            )
        }
    }
}

@Composable
@Preview("History List")
private fun ComposableHistoryListPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            @Suppress("MagicNumber")
            HistoryContainer(
                transactionState =
                    TransactionUiState.Prepared(
                        transactions =
                            persistentListOf(
                                TransactionUi(
                                    TransactionOverviewFixture.new(netValue = Zatoshi(100000000)),
                                    null,
                                    HistoryItemExpandableState.EXPANDED
                                ),
                                TransactionUi(
                                    TransactionOverviewFixture.new(netValue = Zatoshi(200000000)),
                                    null,
                                    HistoryItemExpandableState.COLLAPSED
                                ),
                                TransactionUi(
                                    TransactionOverviewFixture.new(netValue = Zatoshi(300000000)),
                                    null,
                                    HistoryItemExpandableState.COLLAPSED
                                ),
                            )
                    ),
                onTransactionItemAction = {}
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
internal fun HistoryContainer(
    transactionState: TransactionUiState,
    onTransactionItemAction: (TransactionItemAction) -> Unit,
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
            TransactionUiState.Loading, TransactionUiState.Syncing -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))
                    CircularMidProgressIndicator(
                        modifier = Modifier.testTag(HistoryTag.PROGRESS),
                    )
                }
            }
            is TransactionUiState.Prepared -> {
                if (transactionState.transactions.isEmpty()) {
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
                    HistoryList(
                        transactions = transactionState.transactions,
                        onAction = onTransactionItemAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryList(
    transactions: ImmutableList<TransactionUi>,
    onAction: (TransactionItemAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.testTag(HistoryTag.TRANSACTION_LIST)
    ) {
        items(transactions.size) { index ->
            HistoryItem(
                transaction = transactions[index],
                onAction = onAction
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
@Preview("History List Item")
private fun ComposableHistoryListItemPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            @Suppress("MagicNumber")
            HistoryItem(
                onAction = {},
                transaction =
                    TransactionUi(
                        TransactionOverviewFixture.new(netValue = Zatoshi(100000000)),
                        recipient = null,
                        expandableState = HistoryItemExpandableState.EXPANDED
                    )
            )
        }
    }
}

const val ADDRESS_IN_TITLE_WIDTH_RATIO = 0.5f

@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
private fun HistoryItem(
    transaction: TransactionUi,
    onAction: (TransactionItemAction) -> Unit,
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

    Row(
        modifier =
            modifier
                .then(
                    Modifier
                        .background(color = ZcashTheme.colors.historyBackgroundColor)
                        .clickable {
                            if (transaction.expandableState <= HistoryItemExpandableState.COLLAPSED) {
                                onAction(
                                    TransactionItemAction.ExpandableStateChange(
                                        transaction.overview.rawId,
                                        HistoryItemExpandableState.EXPANDED
                                    )
                                )
                            }
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

            if (transaction.expandableState == HistoryItemExpandableState.EXPANDED) {
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                val txId = transaction.overview.txIdString()
                Tiny(
                    text = txId,
                    modifier =
                        Modifier
                            .clickable { onAction(TransactionItemAction.IdClick(txId)) }
                            .testTag(HistoryTag.TRANSACTION_ID)
                )

                Spacer(modifier = (Modifier.height(ZcashTheme.dimens.spacingDefault)))

                // TODO [#1162]: Will be reworked
                // TODO [#1162]: Expandable transaction history item
                // TODO [#1162]: https://github.com/Electric-Coin-Company/zashi-android/issues/1162
                Tiny(
                    text = "Tap to copy message",
                    modifier = Modifier.clickable { onAction(TransactionItemAction.MemoClick(transaction.overview)) }
                )

                Spacer(modifier = (Modifier.height(ZcashTheme.dimens.spacingDefault)))

                Tiny(
                    text = stringResource(id = R.string.account_history_item_collapse_transaction),
                    modifier =
                        Modifier
                            .clickable {
                                if (transaction.expandableState >= HistoryItemExpandableState.EXPANDED) {
                                    onAction(
                                        TransactionItemAction.ExpandableStateChange(
                                            transaction.overview.rawId,
                                            HistoryItemExpandableState.COLLAPSED
                                        )
                                    )
                                }
                            }
                )
            }
        }
    }
}

internal sealed class TransactionItemAction {
    data class IdClick(val id: String) : TransactionItemAction()

    data class ExpandableStateChange(
        val txId: FirstClassByteArray,
        val newState: HistoryItemExpandableState
    ) : TransactionItemAction()

    data class MemoClick(val overview: TransactionOverview) : TransactionItemAction()
}

internal enum class TransactionExtendedState {
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
