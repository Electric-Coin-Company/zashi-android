@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.TransactionState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.extension.DEFAULT_FEE
import cash.z.ecc.sdk.extension.toZecStringAbbreviated
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.SynchronizationStatus
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.CircularMidProgressIndicator
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.TextWithIcon
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTextFieldDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
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
                modifier = Modifier.padding(horizontal = ZashiDimensions.Spacing.spacing3xl)
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
@PreviewScreens
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
@Suppress("LongMethod")
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
            textColor = ZashiColors.Text.textPrimary
            textStyle = ZashiTypography.textSm
        }

        TransactionExtendedState.SENDING -> {
            typeText = stringResource(id = R.string.account_history_item_sending)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_send_icon)
            textColor = ZashiColors.Text.textPrimary
            textStyle = ZashiTypography.textSm
        }

        TransactionExtendedState.SEND_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_send_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_send_icon)
            textColor = ZashiColors.Text.textError
            textStyle =
                ZashiTypography.textSm.copy(
                    textDecoration = TextDecoration.LineThrough
                )
        }

        TransactionExtendedState.RECEIVED -> {
            typeText = stringResource(id = R.string.account_history_item_received)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_receive_icon)
            textColor = ZashiColors.Text.textPrimary
            textStyle = ZashiTypography.textSm
        }

        TransactionExtendedState.RECEIVING -> {
            typeText = stringResource(id = R.string.account_history_item_receiving)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_receive_icon)
            textColor = ZashiColors.Text.textPrimary
            textStyle = ZashiTypography.textSm
        }

        TransactionExtendedState.RECEIVE_FAILED -> {
            typeText = stringResource(id = R.string.account_history_item_receive_failed)
            typeIcon = ImageVector.vectorResource(R.drawable.ic_trx_receive_icon)
            textColor = ZashiColors.Text.textError
            textStyle =
                ZashiTypography.textSm.copy(
                    textDecoration = TextDecoration.LineThrough
                )
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
                        } else {
                            onAction(
                                TrxItemAction.ExpandableStateChange(
                                    transaction.overview.rawId,
                                    TrxItemState.COLLAPSED
                                )
                            )
                        }
                    }
                    .padding(24.dp)
                    .animateContentSize()
            )
    ) {
        Image(
            imageVector = typeIcon,
            colorFilter = ColorFilter.tint(ZashiColors.Text.textPrimary),
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

            Spacer(modifier = Modifier.height(2.dp))

            // To add an extra spacing at the end
            Column {
                val isInExpectedState =
                    transaction.expandableState == TrxItemState.EXPANDED_ADDRESS ||
                        transaction.expandableState == TrxItemState.EXPANDED_ALL

                if (isInExpectedState &&
                    transaction.recipient != null &&
                    transaction.recipient is TransactionRecipient.Address
                ) {
                    HistoryItemExpandedAddressPart(onAction, transaction.recipient, transaction.addressBookContact)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                HistoryItemDatePart(transaction)

                if (transaction.expandableState.isInAnyExtendedState()) {
                    Spacer(modifier = Modifier.height(32.dp))

                    HistoryItemExpandedPart(onAction, transaction)
                }
            }
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag(HistoryTag.TRANSACTION_ITEM_TITLE)
        )

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

        HistoryItemCollapsedAddressPart(onAction, transaction)

        Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

        Spacer(modifier = Modifier.weight(1f))

        val valueTextStyle: TextStyle
        val valueTextColor: Color
        if (transaction.overview.getExtendedState().isFailed()) {
            valueTextStyle =
                ZashiTypography.textSm.copy(
                    textDecoration = TextDecoration.LineThrough
                )
            valueTextColor = ZashiColors.Text.textError
        } else {
            valueTextStyle = ZashiTypography.textSm
            valueTextColor =
                if (transaction.overview.isSentTransaction) {
                    ZashiColors.Text.textError
                } else {
                    ZashiColors.Text.textPrimary
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
                    leastSignificantPart = ZashiTypography.textXxs
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
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .fillMaxWidth(ADDRESS_IN_TITLE_WIDTH_RATIO)
                            .then(clickModifier)
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
    contact: AddressBookContact?,
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
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
            modifier =
                Modifier
                    .fillMaxWidth(EXPANDED_ADDRESS_WIDTH_RATIO)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            TextWithIcon(
                text = stringResource(id = R.string.account_history_item_tap_to_copy),
                style = ZashiTypography.textSm,
                color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
                fontWeight = FontWeight.SemiBold,
                iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
                iconTintColor = ZashiColors.Text.textTertiary,
                modifier =
                    (if (contact == null) Modifier.weight(1f) else Modifier) then
                        Modifier
                            .clickable(
                                role = Role.Button,
                                indication = rememberRipple(radius = 2.dp, color = ZashiColors.Text.textTertiary),
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onAction(TrxItemAction.AddressClick(recipient)) }
            )

            if (contact == null) {
                TextWithIcon(
                    text = stringResource(id = R.string.account_history_item_save_address),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
                    fontWeight = FontWeight.SemiBold,
                    iconVector = ImageVector.vectorResource(R.drawable.ic_trx_save),
                    iconTintColor = ZashiColors.Text.textTertiary,
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable(
                                role = Role.Button,
                                indication = rememberRipple(radius = 2.dp, color = ZashiColors.Text.textTertiary),
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onAction(TrxItemAction.AddToAddressBookClick(recipient)) }
                )
            }
        }
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
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
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
        if (transaction.messages.containsValidMemo()) {
            Text(
                text =
                    pluralStringResource(
                        id = R.plurals.account_history_item_message,
                        count = transaction.messages!!.size
                    ),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
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
        }

        HistoryItemTransactionIdPart(transaction = transaction, onAction = onAction)

        if (transaction.overview.getExtendedState() !in
            listOf(TransactionExtendedState.RECEIVING, TransactionExtendedState.RECEIVED)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HistoryItemTransactionFeePart(fee = transaction.overview.feePaid)
        }
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
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = txIdString,
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
                modifier =
                    Modifier
                        .fillMaxWidth(EXPANDED_TRANSACTION_WIDTH_RATIO)
                        .testTag(HistoryTag.TRANSACTION_ID)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextWithIcon(
                text = stringResource(id = R.string.account_history_item_tap_to_copy),
                style = ZashiTypography.textSm,
                color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
                fontWeight = FontWeight.SemiBold,
                iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
                iconTintColor = ZashiColors.Text.textTertiary,
                modifier =
                    Modifier
                        .clickable(
                            role = Role.Button,
                            indication = rememberRipple(radius = 2.dp, color = ZashiColors.Text.textTertiary),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onAction(TrxItemAction.TransactionIdClick(txIdString)) }
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
            ) {
                Text(
                    text = stringResource(id = R.string.account_history_item_transaction_id),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = txIdString,
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary,
                    maxLines = 1,
                    textAlign = TextAlign.End,
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
    Row(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.account_history_item_transaction_fee),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (fee == null) {
            Text(
                text =
                    stringResource(
                        id = R.string.account_history_item_transaction_fee_typical,
                        DEFAULT_FEE
                    ),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
            )
        } else {
            StyledBalance(
                balanceParts = fee.toZecStringFull().asZecAmountTriple(),
                // Fees are always visible
                isHideBalances = false,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZashiTypography.textSm,
                        leastSignificantPart = ZashiTypography.textXxs
                    ),
                textColor = ZashiColors.Text.textTertiary
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
    Column(modifier = modifier.then(Modifier.fillMaxWidth())) {
        ZashiTextField(
            textStyle =
                if (state.isFailed()) {
                    ZashiTypography.textMd.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.LineThrough
                    )
                } else {
                    ZashiTypography.textMd.copy(fontWeight = FontWeight.Medium)
                },
            state =
                TextFieldState(
                    value = stringRes(message),
                    error =
                        if (state.isFailed()) {
                            stringRes("")
                        } else {
                            null
                        },
                    isEnabled = false
                ) {},
            modifier =
                Modifier
                    .fillMaxWidth(),
            colors =
                ZashiTextFieldDefaults.defaultColors(
                    disabledTextColor =
                        if (state.isFailed()) {
                            ZashiColors.Inputs.ErrorFilled.text
                        } else {
                            ZashiColors.Inputs.Filled.text
                        },
                    disabledHintColor =
                        if (state.isFailed()) {
                            ZashiColors.Inputs.ErrorDefault.hint
                        } else {
                            ZashiColors.Inputs.Disabled.hint
                        },
                    disabledBorderColor =
                        if (state.isFailed()) {
                            ZashiColors.Inputs.ErrorDefault.stroke
                        } else {
                            ZashiColors.Inputs.Disabled.stroke
                        },
                    disabledContainerColor = Color.Transparent,
                    disabledPlaceholderColor =
                        if (state.isFailed()) {
                            ZashiColors.Inputs.ErrorDefault.text
                        } else {
                            ZashiColors.Inputs.Disabled.text
                        },
                ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextWithIcon(
            text = stringResource(id = R.string.account_history_item_tap_to_copy),
            style = ZashiTypography.textSm,
            color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
            fontWeight = FontWeight.SemiBold,
            iconVector = ImageVector.vectorResource(R.drawable.ic_trx_copy),
            iconTintColor = ZashiColors.Text.textTertiary,
            modifier =
                Modifier
                    .clickable(
                        onClick = { onAction(TrxItemAction.MessageClick(message)) },
                        role = Role.Button,
                        indication = rememberRipple(radius = 2.dp, color = ZashiColors.Text.textTertiary),
                        interactionSource = remember { MutableInteractionSource() }
                    )
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

    data class AddToAddressBookClick(val address: TransactionRecipient.Address) : TrxItemAction()

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

    fun isSendType(): Boolean = this == SENDING || this == SENT || this == SEND_FAILED
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
