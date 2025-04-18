@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.balances.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.sdk.extension.DEFAULT_FEE
import cash.z.ecc.sdk.extension.toZecStringFull
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.compose.StatusDialog
import co.electriccoin.zcash.ui.common.compose.SynchronizationStatus
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.changePendingBalance
import co.electriccoin.zcash.ui.common.model.hasChangePending
import co.electriccoin.zcash.ui.common.model.hasValuePending
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.valuePendingBalance
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.CircularSmallProgressIndicator
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.design.component.ZashiModal
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.balances.BalancesTag
import co.electriccoin.zcash.ui.screen.balances.model.ShieldState
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction

@Preview
@Composable
private fun ComposableBalancesPreview() {
    ZcashTheme(forceDarkMode = false) {
        Balances(
            balanceState = BalanceStateFixture.new(),
            isHideBalances = false,
            isShowingErrorDialog = false,
            hideStatusDialog = {},
            showStatusDialog = null,
            setShowErrorDialog = {},
            onShielding = {},
            onStatusClick = {},
            onContactSupport = {},
            shieldState = ShieldState.Available,
            snackbarHostState = SnackbarHostState(),
            walletSnapshot = WalletSnapshotFixture.new(),
            walletRestoringState = WalletRestoringState.NONE,
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }
}

@Preview
@Composable
private fun ComposableBalancesShieldDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        Balances(
            balanceState = BalanceStateFixture.new(),
            isHideBalances = false,
            isShowingErrorDialog = true,
            hideStatusDialog = {},
            showStatusDialog = null,
            setShowErrorDialog = {},
            onShielding = {},
            onStatusClick = {},
            onContactSupport = {},
            shieldState = ShieldState.Available,
            snackbarHostState = SnackbarHostState(),
            walletSnapshot = WalletSnapshotFixture.new(),
            walletRestoringState = WalletRestoringState.NONE,
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }
}

@Preview("BalancesShieldErrorDialog")
@Composable
private fun ComposableBalancesShieldErrorDialogPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            ShieldingErrorDialog(
                state = ShieldState.Failed("Test Error Text", "Test Error Stacktrace"),
                onConfirm = {},
                onReport = {}
            )
        }
    }
}

@Suppress("LongParameterList", "LongMethod")
@Composable
fun Balances(
    balanceState: BalanceState,
    isHideBalances: Boolean,
    isShowingErrorDialog: Boolean,
    hideStatusDialog: () -> Unit,
    onContactSupport: (String?) -> Unit,
    onShielding: () -> Unit,
    onStatusClick: (StatusAction) -> Unit,
    showStatusDialog: StatusAction.Detailed?,
    setShowErrorDialog: (Boolean) -> Unit,
    shieldState: ShieldState,
    snackbarHostState: SnackbarHostState,
    walletSnapshot: WalletSnapshot?,
    walletRestoringState: WalletRestoringState,
    zashiMainTopAppBarState: ZashiMainTopAppBarState?
) {
    BlankBgScaffold(
        topBar = {
            ZashiMainTopAppBar(zashiMainTopAppBarState)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        if (null == walletSnapshot) {
            CircularScreenProgressIndicator()
        } else {
            BalancesMainContent(
                balanceState = balanceState,
                isHideBalances = isHideBalances,
                onShielding = onShielding,
                onStatusClick = onStatusClick,
                walletSnapshot = walletSnapshot,
                shieldState = shieldState,
                modifier =
                    Modifier.scaffoldPadding(paddingValues),
                walletRestoringState = walletRestoringState
            )

            // Show synchronization status popup
            if (showStatusDialog != null) {
                StatusDialog(
                    statusAction = showStatusDialog,
                    onConfirm = hideStatusDialog,
                    onReport = { status ->
                        hideStatusDialog()
                        onContactSupport(status.fullStackTrace)
                    }
                )
            }

            // Show shielding error popup
            if (isShowingErrorDialog) {
                when (shieldState) {
                    is ShieldState.Failed -> {
                        ShieldingErrorDialog(
                            state = shieldState,
                            onConfirm = { setShowErrorDialog(false) },
                            onReport = { state ->
                                setShowErrorDialog(false)
                                onContactSupport(state.stackTrace)
                            }
                        )
                    }

                    ShieldState.FailedGrpc -> {
                        ShieldingErrorGrpcDialog(
                            onComplete = { setShowErrorDialog(false) }
                        )
                    }

                    else -> { // Nothing to do now
                    }
                }
            }
        }
    }
}

@Composable
fun ShieldingErrorDialog(
    state: ShieldState.Failed,
    onConfirm: () -> Unit,
    onReport: (ShieldState.Failed) -> Unit,
) {
    AppAlertDialog(
        title = stringResource(id = R.string.balances_shielding_dialog_error_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.balances_shielding_dialog_error_text),
                    color = ZcashTheme.colors.textPrimary,
                )

                if (state.error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = state.error,
                        fontStyle = FontStyle.Italic,
                        color = ZcashTheme.colors.textPrimary,
                    )
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.balances_shielding_dialog_error_btn),
        onConfirmButtonClick = onConfirm,
        dismissButtonText = stringResource(id = R.string.balances_shielding_dialog_report_btn),
        onDismissButtonClick = { onReport(state) },
    )
}

@Composable
fun ShieldingErrorGrpcDialog(onComplete: () -> Unit) {
    AppAlertDialog(
        title = stringResource(id = R.string.balances_shielding_dialog_error_grpc_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.balances_shielding_dialog_error_grpc_text),
                    color = ZcashTheme.colors.textPrimary,
                )
            }
        },
        confirmButtonText = stringResource(id = R.string.balances_shielding_dialog_error_grpc_btn),
        onConfirmButtonClick = onComplete
    )
}

@Suppress("LongParameterList")
@Composable
private fun BalancesMainContent(
    balanceState: BalanceState,
    isHideBalances: Boolean,
    onShielding: () -> Unit,
    onStatusClick: (StatusAction) -> Unit,
    walletSnapshot: WalletSnapshot,
    shieldState: ShieldState,
    walletRestoringState: WalletRestoringState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        BalanceWidget(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            isReferenceToBalances = false,
            onReferenceClick = {}
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        HorizontalDivider(
            color = ZcashTheme.colors.tertiaryDividerColor,
            thickness = ZcashTheme.dimens.divider
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        BalancesOverview(
            isHideBalances = isHideBalances,
            walletSnapshot = walletSnapshot,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        TransparentBalancePanel(
            isHideBalances = isHideBalances,
            onShielding = onShielding,
            shieldState = shieldState,
            walletSnapshot = walletSnapshot,
        )

        if (walletRestoringState == WalletRestoringState.RESTORING) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            Small(
                text = stringResource(id = R.string.balances_status_restoring_text),
                textFontWeight = FontWeight.Medium,
                color = ZcashTheme.colors.textFieldWarning,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ZcashTheme.dimens.spacingSmall)
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
        } else {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))
        }

        SynchronizationStatus(
            onStatusClick = onStatusClick,
            testTag = BalancesTag.STATUS,
            walletSnapshot = walletSnapshot,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
fun TransparentBalancePanel(
    isHideBalances: Boolean,
    onShielding: () -> Unit,
    shieldState: ShieldState,
    walletSnapshot: WalletSnapshot,
) {
    var showHelpPanel by rememberSaveable { mutableStateOf(false) }
    ZashiModal {
        Column {
            TransparentBalanceRow(
                isHideBalances = isHideBalances,
                isProgressbarVisible = shieldState == ShieldState.Running,
                onHelpClick = { showHelpPanel = !showHelpPanel },
                walletSnapshot = walletSnapshot
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            ZashiButton(
                onClick = onShielding,
                text = stringResource(R.string.balances_transparent_balance_shield),
                enabled = shieldState == ShieldState.Available,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            BodySmall(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text =
                    stringResource(
                        id = R.string.balances_transparent_balance_fee,
                        DEFAULT_FEE
                    ),
                textFontWeight = FontWeight.SemiBold
            )
        }

        if (showHelpPanel) {
            TransparentBalanceHelpPanel(
                onHideHelpPanel = { showHelpPanel = !showHelpPanel }
            )
        }
    }
}

@Composable
fun TransparentBalanceRow(
    isHideBalances: Boolean,
    isProgressbarVisible: Boolean,
    onHelpClick: () -> Unit,
    walletSnapshot: WalletSnapshot,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // To keep both elements together in relatively sized row
        Row(modifier = Modifier.weight(1f)) {
            // Apply common click listener
            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ZcashTheme.dimens.smallRippleEffectCorner))
                        .clickable { onHelpClick() }
            ) {
                BodySmall(text = stringResource(id = R.string.balances_transparent_balance).uppercase())

                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_help_question_mark),
                    contentDescription = stringResource(id = R.string.balances_transparent_help_content_description),
                    modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingXtiny)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            StyledBalance(
                balanceParts = walletSnapshot.transparentBalance.toZecStringFull().asZecAmountTriple(),
                isHideBalances = isHideBalances,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        leastSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textDescriptionDark
            )

            if (isProgressbarVisible) {
                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))
                Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun TransparentBalanceHelpPanel(onHideHelpPanel: () -> Unit) {
    Column(
        modifier =
            Modifier
                .background(color = ZashiColors.Modals.surfacePrimary)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val appName = stringResource(id = R.string.app_name)
        val currencyName = ZcashCurrency.getLocalizedName(LocalContext.current)
        BodySmall(
            text =
                stringResource(
                    id = R.string.balances_transparent_balance_help,
                    appName,
                    currencyName
                ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingDefault)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Reference(
            text = stringResource(id = R.string.balances_transparent_balance_help_close).uppercase(),
            onClick = onHideHelpPanel,
            textStyle = ZcashTheme.extendedTypography.referenceSmall
        )
    }
}

@Composable
fun BalancesOverview(
    walletSnapshot: WalletSnapshot,
    isHideBalances: Boolean,
) {
    Column {
        SpendableBalanceRow(isHideBalances, walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        ChangePendingRow(isHideBalances, walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        //  aka value pending
        PendingTransactionsRow(isHideBalances, walletSnapshot)
    }
}

const val TEXT_PART_WIDTH_RATIO = 0.6f

@Composable
fun SpendableBalanceRow(
    isHideBalances: Boolean,
    walletSnapshot: WalletSnapshot
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodySmall(
            text = stringResource(id = R.string.balances_shielded_spendable).uppercase(),
            modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StyledBalance(
                balanceParts = walletSnapshot.spendableBalance().toZecStringFull().asZecAmountTriple(),
                isHideBalances = isHideBalances,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        leastSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.balance_shield),
                contentDescription = null,
                // The same size as the following progress bars
                modifier = Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)
            )
        }
    }
}

@Composable
fun ChangePendingRow(
    isHideBalances: Boolean,
    walletSnapshot: WalletSnapshot
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodySmall(
            text = stringResource(id = R.string.balances_change_pending).uppercase(),
            modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StyledBalance(
                balanceParts = walletSnapshot.changePendingBalance().toZecStringFull().asZecAmountTriple(),
                isHideBalances = isHideBalances,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        leastSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textDescriptionDark
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (walletSnapshot.hasChangePending()) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PendingTransactionsRow(
    isHideBalances: Boolean,
    walletSnapshot: WalletSnapshot
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodySmall(
            text = stringResource(id = R.string.balances_pending_transactions).uppercase(),
            modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StyledBalance(
                balanceParts = walletSnapshot.valuePendingBalance().toZecStringFull().asZecAmountTriple(),
                isHideBalances = isHideBalances,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        leastSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textDescriptionDark
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (walletSnapshot.hasValuePending()) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}
