@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.home.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.closeDrawerMenu
import co.electriccoin.zcash.ui.common.openDrawerMenu
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyWithFiatCurrencySymbol
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.HeaderWithZecIcon
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.HomeTag
import co.electriccoin.zcash.ui.screen.home.model.CommonTransaction
import co.electriccoin.zcash.ui.screen.home.model.WalletDisplayValues
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Home(
                walletSnapshot = WalletSnapshotFixture.new(),
                transactionHistory = emptyList<CommonTransaction>().toPersistentList(),
                isUpdateAvailable = false,
                isKeepScreenOnDuringSync = false,
                isDebugMenuEnabled = false,
                goSeedPhrase = {},
                goSettings = {},
                goSupport = {},
                goAbout = {},
                goReceive = {},
                goSend = {},
                resetSdk = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun Home(
    walletSnapshot: WalletSnapshot,
    transactionHistory: ImmutableList<CommonTransaction>,
    isUpdateAvailable: Boolean,
    isKeepScreenOnDuringSync: Boolean?,
    isDebugMenuEnabled: Boolean,
    goSeedPhrase: () -> Unit,
    goSettings: () -> Unit,
    goSupport: () -> Unit,
    goAbout: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    resetSdk: () -> Unit,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    scope: CoroutineScope = rememberCoroutineScope()
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
                    isDebugMenuEnabled = isDebugMenuEnabled,
                    openDrawer = { drawerState.openDrawerMenu(scope) },
                    resetSdk = resetSdk
                )
            }) { paddingValues ->
                HomeMainContent(
                    paddingValues,
                    walletSnapshot,
                    transactionHistory,
                    isUpdateAvailable = isUpdateAvailable,
                    isKeepScreenOnDuringSync = isKeepScreenOnDuringSync,
                    goReceive = goReceive,
                    goSend = goSend,
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeTopAppBar(
    isDebugMenuEnabled: Boolean,
    openDrawer: () -> Unit,
    resetSdk: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = openDrawer
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.home_menu_content_description)
                )
            }
        },
        actions = {
            if (isDebugMenuEnabled) {
                DebugMenu(resetSdk)
            }
        }
    )
}

@Composable
private fun DebugMenu(
    resetSdk: () -> Unit
) {
    Column {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Throw Uncaught Exception") },
                onClick = {
                    // Supposed to be generic, for manual debugging only
                    @Suppress("TooGenericExceptionThrown")
                    throw RuntimeException("Manually crashed from debug menu")
                }
            )
            DropdownMenuItem(
                text = { Text("Report Caught Exception") },
                onClick = {
                    // Eventually this shouldn't rely on the Android implementation, but rather an expect/actual
                    // should be used at the crash API level.
                    GlobalCrashReporter.reportCaughtException(RuntimeException("Manually caught exception from debug menu"))
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Reset SDK") },
                onClick = {
                    resetSdk()
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun HomeDrawer(
    onCloseDrawer: () -> Unit,
    goSeedPhrase: () -> Unit,
    goSettings: () -> Unit,
    goSupport: () -> Unit,
    goAbout: () -> Unit,
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
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
    paddingValues: PaddingValues,
    walletSnapshot: WalletSnapshot,
    transactionHistory: ImmutableList<CommonTransaction>,
    isUpdateAvailable: Boolean,
    isKeepScreenOnDuringSync: Boolean?,
    goReceive: () -> Unit,
    goSend: () -> Unit,
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        Status(walletSnapshot, isUpdateAvailable)

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(onClick = goReceive, text = stringResource(R.string.home_button_receive))
        PrimaryButton(onClick = goSend, text = stringResource(R.string.home_button_send))

        History(transactionHistory)

        if (isKeepScreenOnDuringSync == true && isSyncing(walletSnapshot.status)) {
            DisableScreenTimeout()
        }
    }
}

private fun isSyncing(status: Synchronizer.Status): Boolean {
    return status == Synchronizer.Status.DOWNLOADING ||
        status == Synchronizer.Status.VALIDATING ||
        status == Synchronizer.Status.SCANNING ||
        status == Synchronizer.Status.ENHANCING
}

@Composable
@Suppress("LongMethod", "MagicNumber")
private fun Status(
    walletSnapshot: WalletSnapshot,
    updateAvailable: Boolean
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

            // texts
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (walletDisplayValues.zecAmountText.isNotEmpty()) {
                    HeaderWithZecIcon(amount = walletDisplayValues.zecAmountText)
                }

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(24.dp))

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

@Composable
@Suppress("MagicNumber")
private fun History(transactionHistory: ImmutableList<CommonTransaction>) {
    if (transactionHistory.isEmpty()) {
        return
    }

    // here we need to use a fixed height to avoid nested columns vertical scrolling problem
    // we'll refactor this part to a dedicated bottom sheet later
    val historyPart = LocalConfiguration.current.screenHeightDp / 3

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .height(historyPart.dp)
    ) {
        items(transactionHistory) {
            Text(it.toString())
        }
    }
}
