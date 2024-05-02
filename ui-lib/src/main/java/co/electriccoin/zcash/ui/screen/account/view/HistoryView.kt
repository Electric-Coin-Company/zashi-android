@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.SynchronizationStatus
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.CircularMidProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.TextWithIcon
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.HistoryTag
import co.electriccoin.zcash.ui.screen.account.fixture.TransactionUiFixture
import co.electriccoin.zcash.ui.screen.account.fixture.TransactionsFixture
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.model.TrxItemState
import co.electriccoin.zcash.ui.screen.balances.BalancesTag
import co.electriccoin.zcash.ui.screen.send.view.DEFAULT_LESS_THAN_FEE
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
@Preview("History")
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            HistoryContainer(
                onTransactionItemAction = {},
                transactionState = TransactionUiState.Loading,
                walletRestoringState = WalletRestoringState.SYNCING,
                walletSnapshot = WalletSnapshotFixture.new()
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
                transactionState = TransactionUiState.Done(transactions = TransactionsFixture.new()),
                onTransactionItemAction = {},
                walletRestoringState = WalletRestoringState.RESTORING,
                walletSnapshot = WalletSnapshotFixture.new()
            )
        }
    }
}

private val dateFormat: DateFormat by lazy {
    SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.getDefault()
    )
}

@Composable
internal fun HistoryContainer(
    onTransactionItemAction: (TrxItemAction) -> Unit,
    transactionState: TransactionUiState,
    walletRestoringState: WalletRestoringState,
    walletSnapshot: WalletSnapshot,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .then(
                    Modifier
                        .fillMaxSize()
                        .background(ZcashTheme.colors.historyBackgroundColor)
                )
    ) {
        if (walletRestoringState == WalletRestoringState.RESTORING) {
            Column(
                modifier = Modifier.background(color = ZcashTheme.colors.historySyncingColor)
            ) {
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

                // Do not show the app update information and the detailed sync status in the restoring status
                // on Account screen
                SynchronizationStatus(
                    isDetailedStatus = false,
                    isUpdateAvailable = false,
                    testTag = BalancesTag.STATUS,
                    walletSnapshot = walletSnapshot,
                )

                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))
            }
        }
        when (transactionState) {
            TransactionUiState.Loading -> {
                LoadingTransactionHistory()
            }
            TransactionUiState.SyncingEmpty -> {
                if (walletRestoringState == WalletRestoringState.INITIATING) {
                    // In case we are syncing a new wallet, it's empty
                    EmptyTransactionHistory()
                } else {
                    // Intentionally leaving the UI empty otherwise
                }
            }
            is TransactionUiState.Prepared -> {
                HistoryList(
                    transactions = transactionState.transactions,
                    onAction = onTransactionItemAction,
                )
            }
            is TransactionUiState.DoneEmpty -> {
                EmptyTransactionHistory()
            }
        }
    }
}

@Composable
private fun LoadingTransactionHistory() {
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

@Composable
private fun EmptyTransactionHistory() {
    Column {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.account_history_empty),
            style = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular,
            color = ZcashTheme.colors.textCommon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HistoryList(
    transactions: ImmutableList<TransactionUi>,
    onAction: (TrxItemAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.then(Modifier.testTag(HistoryTag.TRANSACTION_LIST))
    ) {
        items(transactions.size) { index ->
            HistoryItem(
                transaction = transactions[index],
                onAction = onAction
            )

            HorizontalDivider(
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
            HistoryItem(
                onAction = {},
                transaction = TransactionUiFixture.new()
            )
        }
    }
}

const val ADDRESS_IN_TITLE_WIDTH_RATIO = 0.5f

@Composable
@Suppress("LongMethod")
private fun HistoryItem(
    transaction: TransactionUi,
    onAction: (TrxItemAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeText: String
    val textColor: Color
    val typeIcon: ImageVector
    val textStyle: TextStyle
    when (transaction.overview.getExtendedState()) {
        TransactionExtendedState.SENT -> {
            typeText = stringResource(id = R.string.account_history_item_sent)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_send_icon)
            textColor = MaterialTheme.colorScheme.onBackground
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular
        }
        TransactionExtendedState.SENDING -> {
            typeText = stringResource(id = R.string.account_history_item_sending)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_send_icon)
            textColor = ZcashTheme.colors.textDescription
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRunning
        }
        TransactionExtendedState.SEND_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_send_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_send_icon)
            textColor = ZcashTheme.colors.historyRedColor
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleFailed
        }

        TransactionExtendedState.RECEIVED -> {
            typeText = stringResource(id = R.string.account_history_item_received)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_receive_icon)
            textColor = MaterialTheme.colorScheme.onBackground
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular
        }
        TransactionExtendedState.RECEIVING -> {
            typeText = stringResource(id = R.string.account_history_item_receiving)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_receive_icon)
            textColor = ZcashTheme.colors.textDescription
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRunning
        }
        TransactionExtendedState.RECEIVE_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_receive_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_receive_icon)
            textColor = ZcashTheme.colors.historyRedColor
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleFailed
        }
    }

    Row(
        modifier =
            modifier.then(
                Modifier
                    .background(color = ZcashTheme.colors.historyBackgroundColor)
                    .clickable {
                        if (transaction.expandableState <= TrxItemState.COLLAPSED) {
                            onAction(
                                TrxItemAction.ExpandableStateChange(
                                    transaction.overview.rawId,
                                    TrxItemState.EXPANDED
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
            contentDescription = typeText
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))

        Column {
            HistoryItemCollapsedMainPart(
                transaction = transaction,
                typeText = typeText,
                textStyle = textStyle,
                textColor = textColor,
                onAction = onAction
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXtiny))

            // To add an extra spacing at the end
            Column(
                modifier = Modifier.padding(end = ZcashTheme.dimens.spacingUpLarge)
            ) {
                val isInExpectedState =
                    transaction.expandableState == TrxItemState.EXPANDED_ADDRESS ||
                        transaction.expandableState == TrxItemState.EXPANDED_ALL

                if (isInExpectedState &&
                    transaction.recipient != null &&
                    transaction.recipient is TransactionRecipient.Address
                ) {
                    HistoryItemExpandedAddressPart(onAction, transaction.recipient)

                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
                }

                HistoryItemDatePart(transaction)

                if (transaction.expandableState >= TrxItemState.EXPANDED) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    HistoryItemExpandedPart(onAction, transaction)
                }
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun HistoryItemCollapsedMainPart(
    transaction: TransactionUi,
    typeText: String,
    textStyle: TextStyle,
    textColor: Color,
    onAction: (TrxItemAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.then(
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 24.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = typeText,
            style = textStyle,
            color = textColor,
            modifier = Modifier.testTag(HistoryTag.TRANSACTION_ITEM_TITLE)
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

        HistoryItemCollapsedAddressPart(onAction, transaction)

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

        Spacer(modifier = Modifier.weight(1f))

        val valueTextStyle: TextStyle
        val valueTextColor: Color
        if (transaction.overview.getExtendedState().isFailed()) {
            valueTextStyle = ZcashTheme.extendedTypography.transactionItemStyles.contentLineThrough
            valueTextColor = ZcashTheme.colors.historyRedColor
        } else {
            valueTextStyle = ZcashTheme.extendedTypography.transactionItemStyles.valueFirstPart
            valueTextColor =
                if (transaction.overview.isSentTransaction) {
                    ZcashTheme.colors.historyRedColor
                } else {
                    ZcashTheme.colors.textCommon
                }
        }

        // TODO [#1047]: Representing Zatoshi amount
        // TODO [#1047]: https://github.com/Electric-Coin-Company/zashi-android/issues/1047
        StyledBalance(
            balanceString = transaction.overview.netValue.toZecString(),
            textStyles =
                Pair(
                    first = valueTextStyle,
                    second = ZcashTheme.extendedTypography.transactionItemStyles.valueSecondPart
                ),
            textColor = valueTextColor,
            prefix =
                if (transaction.overview.isSentTransaction) {
                    stringResource(id = R.string.account_history_item_sent_prefix)
                } else {
                    stringResource(id = R.string.account_history_item_received_prefix)
                }
        )
    }
}

@Composable
private fun HistoryItemCollapsedAddressPart(
    onAction: (TrxItemAction) -> Unit,
    transaction: TransactionUi,
    modifier: Modifier = Modifier
) {
    if (transaction.recipient != null && transaction.recipient is TransactionRecipient.Address) {
        when (transaction.expandableState) {
            TrxItemState.EXPANDED_ADDRESS, TrxItemState.EXPANDED_ALL -> {
                // No address displayed in the top row
            }
            else -> {
                val clickModifier =
                    modifier.then(
                        if (transaction.expandableState <= TrxItemState.COLLAPSED) {
                            Modifier.padding(all = ZcashTheme.dimens.spacingXtiny)
                        } else {
                            Modifier
                                .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                                .clickable {
                                    onAction(
                                        TrxItemAction.ExpandableStateChange(
                                            transaction.overview.rawId,
                                            if (transaction.expandableState == TrxItemState.EXPANDED_ID) {
                                                TrxItemState.EXPANDED_ALL
                                            } else {
                                                TrxItemState.EXPANDED_ADDRESS
                                            }
                                        )
                                    )
                                }
                                .padding(all = ZcashTheme.dimens.spacingXtiny)
                        }
                    )

                Text(
                    text = transaction.recipient.addressValue,
                    style = ZcashTheme.extendedTypography.transactionItemStyles.addressCollapsed,
                    color = ZcashTheme.colors.textDescription,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(ADDRESS_IN_TITLE_WIDTH_RATIO).then(clickModifier)
                )
            }
        }
    } else {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_trx_shielded),
            contentDescription = stringResource(id = R.string.account_history_item_shielded)
        )
    }
}

const val EXPANDED_ADDRESS_WIDTH_RATIO = 0.75f

@Composable
private fun HistoryItemExpandedAddressPart(
    onAction: (TrxItemAction) -> Unit,
    recipient: TransactionRecipient.Address,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier.then(
                Modifier.fillMaxWidth()
            )
    ) {
        Text(
            text = recipient.addressValue,
            style = ZcashTheme.extendedTypography.transactionItemStyles.content,
            color = ZcashTheme.colors.textCommon,
            modifier =
                Modifier
                    .fillMaxWidth(EXPANDED_ADDRESS_WIDTH_RATIO)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        TextWithIcon(
            text = stringResource(id = R.string.account_history_item_tap_to_copy),
            style = ZcashTheme.extendedTypography.transactionItemStyles.content,
            color = ZcashTheme.colors.textDescription,
            iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
            modifier =
                Modifier
                    .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                    .clickable { onAction(TrxItemAction.AddressClick(recipient)) }
                    .padding(all = ZcashTheme.dimens.spacingTiny)
        )
    }
}

@Composable
private fun HistoryItemDatePart(
    transaction: TransactionUi,
    modifier: Modifier = Modifier
) {
    val formattedDate =
        transaction.overview.blockTimeEpochSeconds?.let { blockTimeEpochSeconds ->
            // * 1000 to covert to millis
            @Suppress("MagicNumber")
            dateFormat.format(blockTimeEpochSeconds.times(1000))
        }

    if (formattedDate != null) {
        Text(
            text = formattedDate,
            style = ZcashTheme.extendedTypography.transactionItemStyles.content,
            color = ZcashTheme.colors.textDescription,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }
}

@Composable
private fun HistoryItemExpandedPart(
    onAction: (TrxItemAction) -> Unit,
    transaction: TransactionUi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (transaction.messages.containsValidMemo()) {
            HistoryItemMessagePart(
                messages = transaction.messages!!.toPersistentList(),
                state = transaction.overview.getExtendedState(),
                onAction = onAction
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
        }

        HistoryItemTransactionIdPart(
            transaction = transaction,
            onAction = onAction
        )

        Spacer(modifier = (Modifier.height(ZcashTheme.dimens.spacingDefault)))

        HistoryItemTransactionFeePart(fee = transaction.overview.feePaid)

        Spacer(modifier = (Modifier.height(ZcashTheme.dimens.spacingLarge)))

        TextWithIcon(
            text = stringResource(id = R.string.account_history_item_collapse_transaction),
            style = ZcashTheme.extendedTypography.transactionItemStyles.contentUnderline,
            color = ZcashTheme.colors.textDescription,
            iconVector = ImageVector.vectorResource(id = R.drawable.ic_trx_collapse),
            modifier =
                Modifier
                    .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                    .clickable {
                        if (transaction.expandableState >= TrxItemState.EXPANDED) {
                            onAction(
                                TrxItemAction.ExpandableStateChange(
                                    transaction.overview.rawId,
                                    TrxItemState.COLLAPSED
                                )
                            )
                        }
                    }
                    .padding(all = ZcashTheme.dimens.spacingTiny)
        )
    }
}

private fun List<String>?.containsValidMemo(): Boolean {
    return !isNullOrEmpty() && find { it.isNotEmpty() } != null
}

const val EXPANDED_TRANSACTION_ID_WIDTH_RATIO = 0.75f
const val COLLAPSED_TRANSACTION_ID_WIDTH_RATIO = 0.5f

@Composable
@Suppress("LongMethod")
private fun HistoryItemTransactionIdPart(
    transaction: TransactionUi,
    onAction: (TrxItemAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val txIdString = transaction.overview.txIdString()

    Column(modifier = modifier) {
        if (transaction.expandableState == TrxItemState.EXPANDED_ID ||
            transaction.expandableState == TrxItemState.EXPANDED_ALL
        ) {
            Text(
                text = stringResource(id = R.string.account_history_item_transaction_id),
                style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                color = ZcashTheme.colors.textDescription,
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXtiny))

            Text(
                text = txIdString,
                style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                color = ZcashTheme.colors.textCommon,
                modifier =
                    Modifier
                        .fillMaxWidth(EXPANDED_TRANSACTION_ID_WIDTH_RATIO)
                        .testTag(HistoryTag.TRANSACTION_ID)
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            TextWithIcon(
                text = stringResource(id = R.string.account_history_item_tap_to_copy),
                style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                color = ZcashTheme.colors.textDescription,
                iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                        .clickable { onAction(TrxItemAction.TransactionIdClick(txIdString)) }
                        .padding(all = ZcashTheme.dimens.spacingTiny)
            )
        } else {
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                        .clickable {
                            onAction(
                                TrxItemAction.ExpandableStateChange(
                                    transaction.overview.rawId,
                                    if (transaction.expandableState == TrxItemState.EXPANDED_ADDRESS) {
                                        TrxItemState.EXPANDED_ALL
                                    } else {
                                        TrxItemState.EXPANDED_ID
                                    }
                                )
                            )
                        }
                        .padding(all = ZcashTheme.dimens.spacingTiny)
            ) {
                Text(
                    text = stringResource(id = R.string.account_history_item_transaction_id),
                    style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                    color = ZcashTheme.colors.textDescription,
                )

                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

                Text(
                    text = txIdString,
                    style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                    color = ZcashTheme.colors.textDescription,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .fillMaxWidth(COLLAPSED_TRANSACTION_ID_WIDTH_RATIO)
                            .testTag(HistoryTag.TRANSACTION_ID)
                )
            }
        }
    }
}

@Composable
private fun HistoryItemTransactionFeePart(
    fee: Zatoshi?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.account_history_item_transaction_fee),
            style = ZcashTheme.extendedTypography.transactionItemStyles.content,
            color = ZcashTheme.colors.textDescription,
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

        if (fee == null) {
            Text(
                text =
                    stringResource(
                        id = R.string.account_history_item_transaction_fee_typical,
                        Zatoshi(DEFAULT_LESS_THAN_FEE).toZecString()
                    ),
                style = ZcashTheme.extendedTypography.transactionItemStyles.feeFirstPart,
                color = ZcashTheme.colors.textDescription,
            )
        } else {
            // TODO [#1047]: Representing Zatoshi amount
            // TODO [#1047]: https://github.com/Electric-Coin-Company/zashi-android/issues/1047
            StyledBalance(
                balanceString = fee.toZecString(),
                textStyles =
                    Pair(
                        first = ZcashTheme.extendedTypography.transactionItemStyles.feeFirstPart,
                        second = ZcashTheme.extendedTypography.transactionItemStyles.feeSecondPart
                    ),
                textColor = ZcashTheme.colors.textDescription
            )
        }
    }
}

@Composable
private fun HistoryItemMessagePart(
    messages: ImmutableList<String>,
    state: TransactionExtendedState,
    onAction: (TrxItemAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textStyle: TextStyle
    val textColor: Color
    if (state.isFailed()) {
        textStyle = ZcashTheme.extendedTypography.transactionItemStyles.contentLineThrough
        textColor = ZcashTheme.colors.historyRedColor
    } else {
        textStyle = ZcashTheme.extendedTypography.transactionItemStyles.content
        textColor = ZcashTheme.colors.textCommon
    }

    Column(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Text(
            text = stringResource(id = R.string.account_history_item_message),
            style = ZcashTheme.extendedTypography.transactionItemStyles.contentMedium,
            color = ZcashTheme.colors.textMedium
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = ZcashTheme.colors.textFieldFrame)
        ) {
            // TODO [#1315]: Proper more messages in transaction displaying
            // TODO [#1315]: Note we display the first one only for now
            // TODO [#1315]: https://github.com/Electric-Coin-Company/zashi-android/issues/1315
            Text(
                text = messages[0],
                style = textStyle,
                color = textColor,
                modifier = Modifier.padding(all = ZcashTheme.dimens.spacingMid)
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        TextWithIcon(
            text = stringResource(id = R.string.account_history_item_tap_to_copy),
            style = ZcashTheme.extendedTypography.transactionItemStyles.content,
            color = ZcashTheme.colors.textDescription,
            iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
            modifier =
                Modifier
                    .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                    .clickable { onAction(TrxItemAction.MessageClick(messages[0])) }
                    .padding(all = ZcashTheme.dimens.spacingTiny)
        )
    }
}

internal sealed class TrxItemAction {
    data class TransactionIdClick(val id: String) : TrxItemAction()

    data class ExpandableStateChange(
        val txId: FirstClassByteArray,
        val newState: TrxItemState
    ) : TrxItemAction()

    data class AddressClick(val address: TransactionRecipient.Address) : TrxItemAction()

    data class MessageClick(val memo: String) : TrxItemAction()
}

internal enum class TransactionExtendedState {
    SENT,
    SENDING,
    SEND_FAILED,
    RECEIVED,
    RECEIVING,
    RECEIVE_FAILED;

    fun isFailed(): Boolean = this == SEND_FAILED || this == RECEIVE_FAILED
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
