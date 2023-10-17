@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.home.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.PercentDecimal
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.closeDrawerMenu
import co.electriccoin.zcash.ui.common.openDrawerMenu
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyWithFiatCurrencySymbol
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.HeaderWithZecIcon
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.home.model.WalletDisplayValues
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import kotlinx.coroutines.CoroutineScope

@Preview("Home")
@Composable
private fun ComposablePreview() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            Home(
                walletSnapshot = WalletSnapshotFixture.new(),
                isUpdateAvailable = false,
                isKeepScreenOnDuringSync = false,
                isFiatConversionEnabled = false,
                isCircularProgressBarEnabled = false,
                goSeedPhrase = {},
                goSettings = {},
                goSupport = {},
                goAbout = {},
                goReceive = {},
                goSend = {},
                goHistory = {},
                drawerState = rememberDrawerState(DrawerValue.Closed),
                scope = rememberCoroutineScope()
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
    goSeedPhrase: () -> Unit,
    goSettings: () -> Unit,
    goSupport: () -> Unit,
    goAbout: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawer(
                onCloseDrawer = { drawerState.closeDrawerMenu(scope) },
                goSeedPhrase = goSeedPhrase,
                goSettings = goSettings,
                goSupport = goSupport,
                goAbout = goAbout
            )
        },
        content = {
            Scaffold(topBar = {
                HomeTopAppBar(
                    openDrawer = { drawerState.openDrawerMenu(scope) }
                )
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
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                        start = ZcashTheme.dimens.spacingDefault,
                        end = ZcashTheme.dimens.spacingDefault
                    )
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeTopAppBar(
    openDrawer: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = openDrawer,
                modifier = Modifier.testTag(HomeTag.DRAWER_MENU_OPEN_BUTTON)
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.home_menu_content_description)
                )
            }
        }
    )
}

@Composable
private fun HomeDrawer(
    onCloseDrawer: () -> Unit,
    goSeedPhrase: () -> Unit,
    goSettings: () -> Unit,
    goSupport: () -> Unit,
    goAbout: () -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier.testTag(HomeTag.DRAWER_MENU)
    ) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Password, contentDescription = null) },
            label = { Text(stringResource(id = R.string.home_menu_seed_phrase)) },
            selected = false,
            onClick = {
                onCloseDrawer()
                goSeedPhrase()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text(stringResource(id = R.string.home_menu_settings)) },
            selected = false,
            onClick = {
                onCloseDrawer()
                goSettings()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ContactSupport, contentDescription = null) },
            label = { Text(stringResource(id = R.string.home_menu_support)) },
            selected = false,
            onClick = {
                onCloseDrawer()
                goSupport()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            label = { Text(stringResource(id = R.string.home_menu_about)) },
            selected = false,
            onClick = {
                onCloseDrawer()
                goAbout()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
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

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onClick = goSend,
            text = stringResource(R.string.home_button_send),
            outerPaddingValues = PaddingValues(
                horizontal = ZcashTheme.dimens.spacingNone,
                vertical = ZcashTheme.dimens.spacingSmall
            )
        )
        PrimaryButton(
            onClick = goReceive,
            text = stringResource(R.string.home_button_receive),
            outerPaddingValues = PaddingValues(
                horizontal = ZcashTheme.dimens.spacingNone,
                vertical = ZcashTheme.dimens.spacingSmall
            )
        )

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
