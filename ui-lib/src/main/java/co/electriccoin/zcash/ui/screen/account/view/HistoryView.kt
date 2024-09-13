@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.sdk.extension.DEFAULT_FEE
import cash.z.ecc.sdk.extension.toZecStringAbbreviated
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.SynchronizationStatus
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.BubbleArrowAlignment
import co.electriccoin.zcash.ui.design.component.BubbleMessage
import co.electriccoin.zcash.ui.design.component.CircularMidProgressIndicator
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
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
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
@Preview("History")
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        HistoryContainer(
            onTransactionItemAction = {},
            onStatusClick = {},
            isHideBalances = false,
            transactionState = TransactionUiState.Loading,
            walletRestoringState = WalletRestoringState.SYNCING,
            walletSnapshot = WalletSnapshotFixture.new()
        )
    }
}

@Composable
@Preview("History List")
private fun ComposableHistoryListPreview() {
    ZcashTheme(forceDarkMode = false) {
        HistoryContainer(
            transactionState = TransactionUiState.Done(transactions = TransactionsFixture.new()),
            onTransactionItemAction = {},
            onStatusClick = {},
            isHideBalances = false,
            walletRestoringState = WalletRestoringState.RESTORING,
            walletSnapshot = WalletSnapshotFixture.new()
        )
    }
}

private val dateFormat: DateFormat by lazy {
    SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.getDefault()
    )
}

@Composable
@Suppress("LongParameterList")
internal fun HistoryContainer(
    onStatusClick: (StatusAction) -> Unit,
    onTransactionItemAction: (TrxItemAction) -> Unit,
    isHideBalances: Boolean,
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
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

                // Do not calculate and use the app update information here, as the sync bar won't be displayed after
                // the wallet is fully restored
                SynchronizationStatus(
                    isUpdateAvailable = false,
                    onStatusClick = onStatusClick,
                    testTag = BalancesTag.STATUS,
                    walletSnapshot = walletSnapshot,
                    modifier =
                        Modifier
                            .padding(horizontal = ZcashTheme.dimens.spacingDefault)
                            .animateContentSize()
                )

                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
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
                    isHideBalances = isHideBalances,
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
            color = ZcashTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HistoryList(
    transactions: ImmutableList<TransactionUi>,
    isHideBalances: Boolean,
    onAction: (TrxItemAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.then(Modifier.testTag(HistoryTag.TRANSACTION_LIST))
    ) {
        items(transactions.size) { index ->
            HistoryItem(
                transaction = transactions[index],
                isHideBalances = isHideBalances,
                onAction = onAction
            )

            HorizontalDivider(
                color = ZcashTheme.colors.primaryDividerColor,
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
        BlankSurface {
            HistoryItem(
                onAction = {},
                isHideBalances = false,
                transaction = TransactionUiFixture.new()
            )
        }
    }
}

@Composable
@Preview("History List Item Expanded")
private fun ComposableHistoryListItemExpandedPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Column {
                HistoryItem(
                    onAction = {},
                    isHideBalances = false,
                    transaction =
                        TransactionUiFixture.new(
                            overview = TransactionOverviewFixture.new().copy(isSentTransaction = true),
                            expandableState = TrxItemState.EXPANDED
                        )
                )
                HistoryItem(
                    onAction = {},
                    isHideBalances = false,
                    transaction =
                        TransactionUiFixture.new(
                            overview = TransactionOverviewFixture.new().copy(isSentTransaction = false),
                            expandableState = TrxItemState.EXPANDED
                        )
                )
            }
        }
    }
}

@Preview("Multiple History List Items")
@Composable
private fun ComposableHistoryListItemsPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            HistoryItem(
                onAction = {},
                isHideBalances = false,
                transaction =
                    TransactionUiFixture.new(
                        messages = persistentListOf("Message 1", "Message 2", "Message 3"),
                        expandableState = TrxItemState.EXPANDED
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
    isHideBalances: Boolean,
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

        TransactionExtendedState.SHIELDED -> {
            typeText = stringResource(id = R.string.account_history_item_shielded_funds)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_shielded_funds)
            textColor = MaterialTheme.colorScheme.onBackground
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRegular
        }

        TransactionExtendedState.SHIELDING -> {
            typeText = stringResource(id = R.string.account_history_item_shielding_funds)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_shielded_funds)
            textColor = ZcashTheme.colors.textDescription
            textStyle = ZcashTheme.extendedTypography.transactionItemStyles.titleRunning
        }

        TransactionExtendedState.SHIELDING_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_shielding_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_shielded_funds)
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
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = typeText,
            modifier = Modifier.padding(top = ZcashTheme.dimens.spacingTiny)
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))

        Column {
            HistoryItemCollapsedMainPart(
                transaction = transaction,
                isHideBalances = isHideBalances,
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

                if (transaction.expandableState.isInAnyExtendedState()) {
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
    isHideBalances: Boolean,
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
                    ZcashTheme.colors.textPrimary
                }
        }

        val prefix =
            if (transaction.overview.isSentTransaction) {
                stringResource(id = R.string.account_history_item_sent_prefix)
            } else {
                stringResource(id = R.string.account_history_item_received_prefix)
            }

        StyledBalance(
            balanceParts =
                if (transaction.expandableState.isInAnyExtendedState()) {
                    transaction.overview.netValue.toZecStringFull().asZecAmountTriple(prefix)
                } else {
                    transaction.overview.netValue.toZecStringAbbreviated(
                        suffix = stringResource(id = R.string.general_etc)
                    ).asZecAmountTriple(prefix)
                },
            isHideBalances = isHideBalances,
            textStyle =
                StyledBalanceDefaults.textStyles(
                    mostSignificantPart = valueTextStyle,
                    leastSignificantPart = ZcashTheme.extendedTypography.transactionItemStyles.valueSecondPart
                ),
            textColor = valueTextColor,
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
                    modifier =
                        Modifier
                            .fillMaxWidth(ADDRESS_IN_TITLE_WIDTH_RATIO)
                            .then(clickModifier)
                )
            }
        }
    } else if (!transaction.overview.isShielding) {
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
            color = ZcashTheme.colors.textPrimary,
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
            iconTintColor = ZcashTheme.colors.secondaryColor,
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
@Suppress("LongMethod")
private fun HistoryItemExpandedPart(
    onAction: (TrxItemAction) -> Unit,
    transaction: TransactionUi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (transaction.overview.isShielding.not() && transaction.messages.containsValidMemo()) {
            Text(
                text =
                    pluralStringResource(
                        id = R.plurals.account_history_item_message,
                        count = transaction.messages!!.size
                    ),
                style = ZcashTheme.extendedTypography.transactionItemStyles.contentMedium,
                color = ZcashTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

            // Filter out identical messages on a multi-messages transaction that could be created, e.g., using
            // YWallet, which tends to balance orchard and sapling pools, including by splitting a payment equally
            // across both pools.
            val uniqueMessages = transaction.messages.deduplicateMemos()

            uniqueMessages.forEach { message ->
                HistoryItemMessagePart(
                    message = message,
                    state = transaction.overview.getExtendedState(),
                    onAction = onAction
                )
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
            }
        } else if (transaction.overview.isShielding.not() &&
            (transaction.recipientAddressType == null || transaction.recipientAddressType == AddressType.Shielded)
        ) {
            Text(
                text = stringResource(id = R.string.account_history_item_no_message),
                style = ZcashTheme.extendedTypography.transactionItemStyles.contentItalic,
                color = ZcashTheme.colors.textPrimary,
                modifier = Modifier.fillMaxWidth(EXPANDED_TRANSACTION_WIDTH_RATIO)
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
    return !isNullOrEmpty() && any { it.isNotEmpty() }
}

private fun List<String>.deduplicateMemos(): List<String> {
    return distinct()
}

const val EXPANDED_TRANSACTION_WIDTH_RATIO = 0.75f
const val COLLAPSED_TRANSACTION_WIDTH_RATIO = 0.5f

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

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            Text(
                text = txIdString,
                style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                color = ZcashTheme.colors.textPrimary,
                modifier =
                    Modifier
                        .fillMaxWidth(EXPANDED_TRANSACTION_WIDTH_RATIO)
                        .testTag(HistoryTag.TRANSACTION_ID)
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

            TextWithIcon(
                text = stringResource(id = R.string.account_history_item_tap_to_copy),
                style = ZcashTheme.extendedTypography.transactionItemStyles.content,
                color = ZcashTheme.colors.textDescription,
                iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
                iconTintColor = ZcashTheme.colors.secondaryColor,
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
                            .fillMaxWidth(COLLAPSED_TRANSACTION_WIDTH_RATIO)
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

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingTiny))

        if (fee == null) {
            Text(
                text =
                    stringResource(
                        id = R.string.account_history_item_transaction_fee_typical,
                        DEFAULT_FEE
                    ),
                style = ZcashTheme.extendedTypography.transactionItemStyles.feeFirstPart,
                color = ZcashTheme.colors.textDescription,
            )
        } else {
            StyledBalance(
                balanceParts = fee.toZecStringFull().asZecAmountTriple(),
                // Fees are always visible
                isHideBalances = false,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.transactionItemStyles.feeFirstPart,
                        leastSignificantPart = ZcashTheme.extendedTypography.transactionItemStyles.feeSecondPart
                    ),
                textColor = ZcashTheme.colors.textDescription
            )
        }
    }
}

@Composable
private fun HistoryItemMessagePart(
    message: String,
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
        textColor = ZcashTheme.colors.textPrimary
    }

    Column(modifier = modifier.then(Modifier.fillMaxWidth())) {
        val bubbleBackgroundColor: Color
        val bubbleStroke: BorderStroke
        val arrowAlignment: BubbleArrowAlignment
        if (state.isSendType()) {
            bubbleBackgroundColor = Color.Transparent
            bubbleStroke = BorderStroke(1.dp, ZcashTheme.colors.textFieldFrame)
            arrowAlignment = BubbleArrowAlignment.BottomLeft
        } else {
            bubbleBackgroundColor = ZcashTheme.colors.historyMessageBubbleColor
            bubbleStroke = BorderStroke(1.dp, ZcashTheme.colors.historyMessageBubbleStrokeColor)
            arrowAlignment = BubbleArrowAlignment.BottomRight
        }

        BubbleMessage(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = bubbleBackgroundColor,
            borderStroke = bubbleStroke,
            arrowAlignment = arrowAlignment
        ) {
            Text(
                text = message,
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
            iconTintColor = ZcashTheme.colors.secondaryColor,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                    .clickable { onAction(TrxItemAction.MessageClick(message)) }
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
    RECEIVE_FAILED,
    SHIELDED,
    SHIELDING,
    SHIELDING_FAILED;

    fun isShielding() = this in listOf(SHIELDED, RECEIVE_FAILED, SHIELDING_FAILED)

    fun isFailed(): Boolean = this in listOf(SEND_FAILED, RECEIVE_FAILED, SHIELDING_FAILED)

    fun isSendType(): Boolean = this in listOf(SENDING, SENT, SEND_FAILED, SHIELDED, SHIELDING_FAILED, SHIELDING)
}

private fun TransactionOverview.getExtendedState(): TransactionExtendedState {
    return when (transactionState) {
        TransactionState.Expired ->
            when {
                isShielding -> TransactionExtendedState.SHIELDING_FAILED
                isSentTransaction -> TransactionExtendedState.SEND_FAILED
                else -> TransactionExtendedState.RECEIVE_FAILED
            }

        TransactionState.Confirmed ->
            when {
                isShielding -> TransactionExtendedState.SHIELDED
                isSentTransaction -> TransactionExtendedState.SENT
                else -> TransactionExtendedState.RECEIVED
            }

        TransactionState.Pending ->
            when {
                isShielding -> TransactionExtendedState.SHIELDING
                isSentTransaction -> TransactionExtendedState.SENDING
                else -> TransactionExtendedState.RECEIVING
            }

        else -> error("Unexpected transaction state found while calculating its extended state.")
    }
}
