package cash.z.ecc.ui.screen.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.db.entity.Transaction
import cash.z.ecc.sdk.model.toZecString
import cash.z.ecc.sdk.model.total
import cash.z.ecc.ui.R
import cash.z.ecc.ui.fixture.WalletSnapshotFixture
import cash.z.ecc.ui.screen.common.Body
import cash.z.ecc.ui.screen.common.GradientSurface
import cash.z.ecc.ui.screen.common.Header
import cash.z.ecc.ui.screen.common.PrimaryButton
import cash.z.ecc.ui.screen.common.TertiaryButton
import cash.z.ecc.ui.screen.home.model.WalletSnapshot
import cash.z.ecc.ui.screen.home.model.totalBalance
import cash.z.ecc.ui.theme.MINIMAL_WEIGHT
import cash.z.ecc.ui.theme.ZcashTheme

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
                goRequest = {}
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun Home(
    walletSnapshot: WalletSnapshot,
    transactionHistory: List<Transaction>,
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    Scaffold(topBar = {
        HomeTopAppBar()
    }) {
        HomeMainContent(
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
private fun HomeTopAppBar() {
    TopAppBar {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.app_name))
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun HomeMainContent(
    walletSnapshot: WalletSnapshot,
    transactionHistory: List<Transaction>,
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    Column {
        Row(Modifier.fillMaxWidth()) {
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
