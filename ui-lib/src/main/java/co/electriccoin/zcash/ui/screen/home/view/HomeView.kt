package co.electriccoin.zcash.ui.screen.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.db.entity.Transaction
import cash.z.ecc.sdk.ext.ui.model.toZecString
import cash.z.ecc.sdk.model.total
import co.electriccoin.zcash.crash.android.CrashReporter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import co.electriccoin.zcash.ui.screen.home.model.totalBalance
import java.lang.RuntimeException

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Home(
                WalletSnapshotFixture.new(),
                emptyList(),
                goScan = {},
                goProfile = {},
                goSend = {},
                goRequest = {},
                isDebugMenuEnabled = false,
                resetSdk = {},
                wipeEntireWallet = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun Home(
    walletSnapshot: WalletSnapshot,
    transactionHistory: List<Transaction>,
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit,
    resetSdk: () -> Unit,
    wipeEntireWallet: () -> Unit,
    isDebugMenuEnabled: Boolean
) {
    Scaffold(topBar = {
        HomeTopAppBar(isDebugMenuEnabled, resetSdk, wipeEntireWallet)
    }) { paddingValues ->
        HomeMainContent(
            paddingValues,
            walletSnapshot,
            transactionHistory,
            goScan = goScan,
            goProfile = goProfile,
            goSend = goSend,
            goRequest = goRequest
        )
    }
}

@Composable
private fun HomeTopAppBar(
    isDebugMenuEnabled: Boolean,
    resetSdk: () -> Unit,
    wipeEntireWallet: () -> Unit
) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            if (isDebugMenuEnabled) {
                DebugMenu(resetSdk, wipeEntireWallet)
            }
        }
    )
}

@Composable
private fun DebugMenu(resetSdk: () -> Unit, wipeEntireWallet: () -> Unit) {
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
                CrashReporter.reportCaughtException(RuntimeException("Manually caught exception from debug menu"))
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
        DropdownMenuItem(
            text = { Text("Wipe entire wallet") },
            onClick = {
                wipeEntireWallet()
                expanded = false
            }
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun HomeMainContent(
    paddingValues: PaddingValues,
    walletSnapshot: WalletSnapshot,
    transactionHistory: List<Transaction>,
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            IconButton(goScan) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = stringResource(R.string.home_scan_content_description)
                )
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(MINIMAL_WEIGHT)
            )
            IconButton(goProfile) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(R.string.home_profile_content_description)
                )
            }
        }
        Status(walletSnapshot)
        PrimaryButton(onClick = goSend, text = stringResource(R.string.home_button_send))
        TertiaryButton(onClick = goRequest, text = stringResource(R.string.home_button_request))
        History(transactionHistory)
    }
}

@Composable
private fun Status(walletSnapshot: WalletSnapshot) {
    Column(Modifier.fillMaxWidth()) {
        Header(text = walletSnapshot.totalBalance().toZecString())
        Body(
            text = stringResource(
                id = R.string.home_status_shielding_format,
                walletSnapshot.saplingBalance.total.toZecString()
            )
        )
    }
}

@Composable
private fun History(transactionHistory: List<Transaction>) {
    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            items(transactionHistory) {
                Text(it.toString())
            }
        }
    }
}
