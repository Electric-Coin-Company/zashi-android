package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyWithFiatCurrencySymbol
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.HeaderWithZecIcon
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.model.WalletDisplayValues

@Preview("Account")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Account(
                walletSnapshot = WalletSnapshotFixture.new(),
                isUpdateAvailable = false,
                isKeepScreenOnDuringSync = false,
                isFiatConversionEnabled = false,
                goSettings = {},
                goReceive = {},
                goSend = {},
                goHistory = {}
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun Account(
    walletSnapshot: WalletSnapshot,
    isUpdateAvailable: Boolean,
    isKeepScreenOnDuringSync: Boolean?,
    isFiatConversionEnabled: Boolean,
    goSettings: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit
) {
    Scaffold(topBar = {
        AccountTopAppBar(onSettings = goSettings)
    }) { paddingValues ->
        AccountMainContent(
            walletSnapshot = walletSnapshot,
            isUpdateAvailable = isUpdateAvailable,
            isKeepScreenOnDuringSync = isKeepScreenOnDuringSync,
            isFiatConversionEnabled = isFiatConversionEnabled,
            goReceive = goReceive,
            goSend = goSend,
            goHistory = goHistory,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                start = ZcashTheme.dimens.screenHorizontalSpacing,
                end = ZcashTheme.dimens.screenHorizontalSpacing
            )
        )
    }
}

@Composable
private fun AccountTopAppBar(
    onSettings: () -> Unit
) {
    SmallTopAppBar(
        showTitleLogo = true,
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(AccountTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.account_menu_content_description)
                )
            }
        }
    )
}

@Suppress("LongParameterList")
@Composable
private fun AccountMainContent(
    walletSnapshot: WalletSnapshot,
    isUpdateAvailable: Boolean,
    isKeepScreenOnDuringSync: Boolean?,
    isFiatConversionEnabled: Boolean,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(
                rememberScrollState()
            )
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Status(walletSnapshot, isUpdateAvailable, isFiatConversionEnabled)

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        PrimaryButton(
            onClick = goSend,
            text = stringResource(R.string.account_button_send)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        PrimaryButton(
            onClick = goReceive,
            text = stringResource(R.string.account_button_receive)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        TertiaryButton(onClick = goHistory, text = stringResource(R.string.account_button_history))

        if (isKeepScreenOnDuringSync == true && walletSnapshot.status == Synchronizer.Status.SYNCING) {
            DisableScreenTimeout()
        }
    }
}

@Composable
@Suppress("LongMethod", "MagicNumber")
private fun Status(
    walletSnapshot: WalletSnapshot,
    updateAvailable: Boolean,
    isFiatConversionEnabled: Boolean
) {
    val walletDisplayValues = WalletDisplayValues.getNextValues(
        LocalContext.current,
        walletSnapshot,
        updateAvailable
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(AccountTag.STATUS_VIEWS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        if (walletDisplayValues.zecAmountText.isNotEmpty()) {
            HeaderWithZecIcon(amount = walletDisplayValues.zecAmountText)
        }

        if (isFiatConversionEnabled) {
            Column(Modifier.testTag(AccountTag.FIAT_CONVERSION)) {
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

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        if (walletDisplayValues.statusText.isNotEmpty()) {
            Body(
                text = walletDisplayValues.statusText,
                modifier = Modifier.testTag(AccountTag.SINGLE_LINE_TEXT)
            )
        }
    }
}
