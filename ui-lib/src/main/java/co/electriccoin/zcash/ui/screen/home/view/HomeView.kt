@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.home.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.PercentDecimal
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
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
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.home.model.WalletDisplayValues
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot

@Preview("Home")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Home(
                walletSnapshot = WalletSnapshotFixture.new(),
                isUpdateAvailable = false,
                isKeepScreenOnDuringSync = false,
                isFiatConversionEnabled = false,
                isCircularProgressBarEnabled = false,
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
fun Home(
    walletSnapshot: WalletSnapshot,
    isUpdateAvailable: Boolean,
    isKeepScreenOnDuringSync: Boolean?,
    isFiatConversionEnabled: Boolean,
    isCircularProgressBarEnabled: Boolean,
    goSettings: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit
) {
    Scaffold(topBar = {
        HomeTopAppBar(onSettings = goSettings)
    }) { paddingValues ->
        HomeMainContent(
            walletSnapshot = walletSnapshot,
            isUpdateAvailable = isUpdateAvailable,
            isKeepScreenOnDuringSync = isKeepScreenOnDuringSync,
            isFiatConversionEnabled = isFiatConversionEnabled,
            isCircularProgressBarEnabled = isCircularProgressBarEnabled,
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
private fun HomeTopAppBar(
    onSettings: () -> Unit
) {
    SmallTopAppBar(
        showTitleLogo = true,
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(HomeTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.home_menu_content_description)
                )
            }
        }
    )
}

@Suppress("LongParameterList")
@Composable
private fun HomeMainContent(
    walletSnapshot: WalletSnapshot,
    isUpdateAvailable: Boolean,
    isKeepScreenOnDuringSync: Boolean?,
    isFiatConversionEnabled: Boolean,
    isCircularProgressBarEnabled: Boolean,
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
        Status(walletSnapshot, isUpdateAvailable, isFiatConversionEnabled, isCircularProgressBarEnabled)

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        PrimaryButton(
            onClick = goSend,
            text = stringResource(R.string.home_button_send)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        PrimaryButton(
            onClick = goReceive,
            text = stringResource(R.string.home_button_receive)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        TertiaryButton(onClick = goHistory, text = stringResource(R.string.home_button_history))

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
    isFiatConversionEnabled: Boolean,
    isCircularProgressBarEnabled: Boolean
) {
    val configuration = LocalConfiguration.current
    val contentSizeRatioRatio = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        0.45f
    } else {
        0.9f
    }

    // UI parts sizes
    val progressCircleStroke = 12.dp
    val progressCirclePadding = progressCircleStroke + 6.dp
    val contentPadding = progressCircleStroke + progressCirclePadding + 10.dp

    val walletDisplayValues = WalletDisplayValues.getNextValues(
        LocalContext.current,
        walletSnapshot,
        updateAvailable
    )

    // wrapper box
    Box(
        Modifier
            .fillMaxWidth()
            .testTag(HomeTag.STATUS_VIEWS),
        contentAlignment = Alignment.Center
    ) {
        // relatively sized box
        Box(
            modifier = Modifier
                .fillMaxWidth(contentSizeRatioRatio)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            // progress circle
            if (isCircularProgressBarEnabled) {
                if (walletDisplayValues.progress.decimal > PercentDecimal.ZERO_PERCENT.decimal) {
                    CircularProgressIndicator(
                        progress = walletDisplayValues.progress.decimal,
                        color = Color.Gray,
                        strokeWidth = progressCircleStroke,
                        modifier = Modifier
                            .matchParentSize()
                            .padding(progressCirclePadding)
                            .testTag(HomeTag.PROGRESS)
                    )
                }
            }
            // texts
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

                if (walletDisplayValues.zecAmountText.isNotEmpty()) {
                    HeaderWithZecIcon(amount = walletDisplayValues.zecAmountText)
                }

                if (isFiatConversionEnabled) {
                    Column(Modifier.testTag(HomeTag.FIAT_CONVERSION)) {
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
                        modifier = Modifier.testTag(HomeTag.SINGLE_LINE_TEXT)
                    )
                }
            }
        }
    }
}
