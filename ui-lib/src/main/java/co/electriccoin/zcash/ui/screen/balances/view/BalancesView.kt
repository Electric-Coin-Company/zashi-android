@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.balances.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.extension.toPercentageWithDecimal
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.compose.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.changePendingBalance
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.valuePendingBalance
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.BodyWithFiatCurrencySymbol
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.CircularSmallProgressIndicator
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.LinearProgressIndicator
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.balances.BalancesTag
import co.electriccoin.zcash.ui.screen.balances.model.ShieldState
import co.electriccoin.zcash.ui.screen.balances.model.WalletDisplayValues

@Preview("Balances")
@Composable
private fun ComposableBalancesPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Balances(
                onSettings = {},
                isFiatConversionEnabled = false,
                isKeepScreenOnWhileSyncing = false,
                isUpdateAvailable = false,
                onShielding = {},
                shieldState = ShieldState.Available,
                walletSnapshot = WalletSnapshotFixture.new(),
                isShowingErrorDialog = false,
                setShowErrorDialog = {},
            )
        }
    }
}

@Preview("BalancesShieldFailure")
@Composable
private fun ComposableBalancesShieldFailurePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Balances(
                onSettings = {},
                isFiatConversionEnabled = false,
                isKeepScreenOnWhileSyncing = false,
                isUpdateAvailable = false,
                onShielding = {},
                shieldState = ShieldState.Available,
                walletSnapshot = WalletSnapshotFixture.new(),
                isShowingErrorDialog = true,
                setShowErrorDialog = {},
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun Balances(
    onSettings: () -> Unit,
    isFiatConversionEnabled: Boolean,
    isKeepScreenOnWhileSyncing: Boolean?,
    isUpdateAvailable: Boolean,
    isShowingErrorDialog: Boolean,
    setShowErrorDialog: (Boolean) -> Unit,
    onShielding: () -> Unit,
    shieldState: ShieldState,
    walletSnapshot: WalletSnapshot?,
) {
    Scaffold(topBar = {
        BalancesTopAppBar(onSettings = onSettings)
    }) { paddingValues ->
        if (null == walletSnapshot) {
            CircularScreenProgressIndicator()
        } else {
            BalancesMainContent(
                isFiatConversionEnabled = isFiatConversionEnabled,
                isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
                isUpdateAvailable = isUpdateAvailable,
                onShielding = onShielding,
                walletSnapshot = walletSnapshot,
                shieldState = shieldState,
                modifier =
                    Modifier.padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    ),
            )

            // Show shielding error popup
            if (isShowingErrorDialog && shieldState is ShieldState.Failed) {
                ShieldingErrorDialog(
                    reason = shieldState.error,
                    onDone = { setShowErrorDialog(false) }
                )
            }
        }
    }
}

@Composable
fun ShieldingErrorDialog(
    reason: String,
    onDone: () -> Unit
) {
    // TODO [#1276]: Once we ensure that reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    AppAlertDialog(
        title = stringResource(id = R.string.balances_shielding_dialog_error_title),
        text = {
            Column {
                Text(text = stringResource(id = R.string.balances_shielding_dialog_error_text))

                if (reason.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = reason,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.balances_shielding_dialog_error_btn),
        onConfirmButtonClick = onDone
    )
}

@Composable
private fun BalancesTopAppBar(onSettings: () -> Unit) {
    SmallTopAppBar(
        showTitleLogo = false,
        titleText = stringResource(id = R.string.balances_title),
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        }
    )
}

@Suppress("LongParameterList")
@Composable
private fun BalancesMainContent(
    isFiatConversionEnabled: Boolean,
    isKeepScreenOnWhileSyncing: Boolean?,
    isUpdateAvailable: Boolean,
    onShielding: () -> Unit,
    walletSnapshot: WalletSnapshot,
    shieldState: ShieldState,
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
            walletSnapshot = walletSnapshot,
            isReferenceToBalances = false,
            onReferenceClick = {}
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))

        Divider(
            color = ZcashTheme.colors.darkDividerColor,
            thickness = ZcashTheme.dimens.divider
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        BalancesOverview(
            walletSnapshot = walletSnapshot,
            isFiatConversionEnabled = isFiatConversionEnabled,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        TransparentBalancePanel(
            onShielding = onShielding,
            shieldState = shieldState,
            walletSnapshot = walletSnapshot,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(modifier = Modifier.weight(1f, true))

        SyncStatus(
            walletSnapshot = walletSnapshot,
            isUpdateAvailable = isUpdateAvailable,
        )

        if (isKeepScreenOnWhileSyncing == true && walletSnapshot.status == Synchronizer.Status.SYNCING) {
            DisableScreenTimeout()
        }
    }
}

@Composable
fun TransparentBalancePanel(
    onShielding: () -> Unit,
    shieldState: ShieldState,
    walletSnapshot: WalletSnapshot,
) {
    var showHelpPanel by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .background(color = ZcashTheme.colors.panelBackgroundColor)
                .wrapContentSize()
                .animateContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            TransparentBalanceRow(
                isProgressbarVisible = shieldState == ShieldState.Running,
                onHelpClick = { showHelpPanel = !showHelpPanel },
                walletSnapshot = walletSnapshot
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            PrimaryButton(
                onClick = onShielding,
                text = stringResource(R.string.balances_transparent_balance_shield),
                textStyle = ZcashTheme.extendedTypography.buttonTextSmall,
                enabled = shieldState == ShieldState.Available,
                minHeight = ZcashTheme.dimens.buttonHeightSmall,
                modifier = Modifier.fillMaxWidth(),
                outerPaddingValues =
                    PaddingValues(
                        horizontal = 54.dp,
                        vertical = ZcashTheme.dimens.spacingSmall
                    )
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            BodySmall(
                text =
                    stringResource(
                        id = R.string.balances_transparent_balance_fee,
                        // TODO [#1047]: Representing Zatoshi amount
                        // TODO [#1047]: https://github.com/Electric-Coin-Company/zashi-android/issues/1047
                        @Suppress("MagicNumber")
                        Zatoshi(100_000L).toZecString()
                    ),
                textFontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))
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
    isProgressbarVisible: Boolean,
    onHelpClick: () -> Unit,
    walletSnapshot: WalletSnapshot,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = ZcashTheme.dimens.spacingDefault,
                    end = ZcashTheme.dimens.spacingSmall
                ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // To keep both elements together in relatively sized row
        Row(modifier = Modifier.fillMaxWidth(TEXT_PART_WIDTH_RATIO)) {
            // Apply common click listener
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(ZcashTheme.dimens.smallRippleEffectCorner))
                        .clickable { onHelpClick() }
                        .padding(end = ZcashTheme.dimens.spacingXtiny)
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
                balanceString = walletSnapshot.transparentBalance.toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textPending
            )

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (isProgressbarVisible) {
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
                .padding(all = ZcashTheme.dimens.spacingDefault)
                .background(color = Color.White)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        BodySmall(
            text = stringResource(id = R.string.balances_transparent_balance_help),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingDefault)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Reference(
            text = stringResource(id = R.string.balances_transparent_balance_help_close).uppercase(),
            onClick = onHideHelpPanel,
            textStyle = ZcashTheme.extendedTypography.referenceSmall
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
    }
}

@Composable
fun BalancesOverview(
    walletSnapshot: WalletSnapshot,
    isFiatConversionEnabled: Boolean,
) {
    Column {
        SpendableBalanceRow(walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        ChangePendingRow(walletSnapshot)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        //  aka value pending
        PendingTransactionsRow(walletSnapshot)

        if (isFiatConversionEnabled) {
            val walletDisplayValues =
                WalletDisplayValues.getNextValues(
                    LocalContext.current,
                    walletSnapshot,
                    false
                )

            Column(Modifier.testTag(BalancesTag.FIAT_CONVERSION)) {
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

                when (walletDisplayValues.fiatCurrencyAmountState) {
                    is FiatCurrencyConversionRateState.Current -> {
                        BodyWithFiatCurrencySymbol(
                            amount = walletDisplayValues.fiatCurrencyAmountText
                        )
                    }
                    is FiatCurrencyConversionRateState.Stale -> {
                        // Note: we should show information about staleness too
                        BodyWithFiatCurrencySymbol(
                            amount = walletDisplayValues.fiatCurrencyAmountText
                        )
                    }
                    is FiatCurrencyConversionRateState.Unavailable -> {
                        Body(text = walletDisplayValues.fiatCurrencyAmountText)
                    }
                }
            }
        }
    }
}

const val TEXT_PART_WIDTH_RATIO = 0.6f

@Composable
fun SpendableBalanceRow(walletSnapshot: WalletSnapshot) {
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
                balanceString = walletSnapshot.spendableBalance().toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textCommon
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
fun ChangePendingRow(walletSnapshot: WalletSnapshot) {
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
            val changePendingHasValue = walletSnapshot.changePendingBalance().value > 0L

            StyledBalance(
                balanceString = walletSnapshot.changePendingBalance().toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textPending
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (changePendingHasValue) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PendingTransactionsRow(walletSnapshot: WalletSnapshot) {
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
            val valuePendingHasValue = walletSnapshot.valuePendingBalance().value > 0L

            StyledBalance(
                balanceString = walletSnapshot.valuePendingBalance().toZecString(),
                textStyles =
                    Pair(
                        ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
                textColor = ZcashTheme.colors.textPending
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(Modifier.width(ZcashTheme.dimens.circularSmallProgressWidth)) {
                if (valuePendingHasValue) {
                    CircularSmallProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun SyncStatus(
    isUpdateAvailable: Boolean,
    walletSnapshot: WalletSnapshot,
) {
    val walletDisplayValues =
        WalletDisplayValues.getNextValues(
            LocalContext.current,
            walletSnapshot,
            isUpdateAvailable
        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (walletDisplayValues.statusText.isNotEmpty()) {
            BodySmall(
                text = walletDisplayValues.statusText,
                modifier = Modifier.testTag(BalancesTag.STATUS)
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
        }

        BodySmall(
            text =
                stringResource(
                    id = R.string.balances_status_syncing_percentage,
                    walletSnapshot.progress.toPercentageWithDecimal()
                ),
            textFontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        LinearProgressIndicator(
            progress = walletSnapshot.progress.decimal,
            modifier =
                Modifier.padding(
                    horizontal = ZcashTheme.dimens.spacingUpLarge
                )
        )
    }
}
