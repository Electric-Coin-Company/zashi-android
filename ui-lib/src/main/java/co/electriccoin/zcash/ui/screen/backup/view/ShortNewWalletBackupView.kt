@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.SecureScreen
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.ChipGrid
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.collections.immutable.toPersistentList

@Preview(device = Devices.PIXEL_4)
@Composable
fun ComposablePreviewShort() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            ShortNewWalletBackup(
                PersistableWalletFixture.new(),
                onCopyToClipboard = {},
                onComplete = {},
            )
        }
    }
}

/**
 * @param onComplete Callback when the user has confirmed viewing the seed phrase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortNewWalletBackup(
    wallet: PersistableWallet,
    onCopyToClipboard: () -> Unit,
    onComplete: () -> Unit,
) {
    Scaffold(
        topBar = {
            ShortNewWalletTopAppBar(
                onCopyToClipboard = onCopyToClipboard,
            )
        },
        bottomBar = {
            ShortNewWalletBottomNav(
                onComplete = onComplete,
            )
        }
    ) { paddingValues ->
        ShortNewWalletMainContent(
            paddingValues = paddingValues,
            wallet = wallet,
        )
    }
}

@Composable
private fun ShortNewWalletMainContent(
    paddingValues: PaddingValues,
    wallet: PersistableWallet,
) {
    Column(
        Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        SeedPhrase(wallet)
    }
}

@Composable
private fun SeedPhrase(persistableWallet: PersistableWallet) {
    SecureScreen()
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = ZcashTheme.paddings.padding)
    ) {
        Body(stringResource(R.string.new_wallet_short_body))
        ChipGrid(persistableWallet.seedPhrase.split.toPersistentList())
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ShortNewWalletTopAppBar(
    onCopyToClipboard: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.new_wallet_short_header)) },
        actions = {
            CopySeedMenu(onCopyToClipboard)
        }
    )
}

@Composable
private fun CopySeedMenu(onCopyToClipboard: () -> Unit) {
    Column {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.new_wallet_toolbar_more_button_content_description)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.new_wallet_short_copy)) },
                onClick = {
                    expanded = false
                    onCopyToClipboard()
                }
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun ShortNewWalletBottomNav(
    onComplete: () -> Unit
) {
    Column {
        PrimaryButton(onClick = onComplete, text = stringResource(R.string.new_wallet_short_button_finished))
    }
}
