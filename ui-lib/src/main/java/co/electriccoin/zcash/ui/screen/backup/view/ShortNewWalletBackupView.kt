@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.SecureScreen
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.ChipGrid
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.collections.immutable.toPersistentList

@Preview(name = "ShortNewWalletBackup", device = Devices.PIXEL_4)
@Composable
private fun ComposablePreviewShort() {
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
                modifier = Modifier.padding(
                    bottom = ZcashTheme.dimens.spacingHuge
                ).fillMaxWidth(),
            )
        }
    ) { paddingValues ->
        ShortNewWalletMainContent(
            wallet = wallet,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                start = ZcashTheme.dimens.spacingDefault,
                end = ZcashTheme.dimens.spacingDefault
            )
        )
    }
}

@Composable
private fun ShortNewWalletMainContent(
    wallet: PersistableWallet,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(
                rememberScrollState()
            )
            .then(modifier)
    ) {
        SeedPhrase(wallet)
    }
}

@Composable
private fun SeedPhrase(persistableWallet: PersistableWallet) {
    if (BuildConfig.IS_SECURE_SCREEN_ENABLED) {
        SecureScreen()
    }
    Column {
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
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(onClick = onComplete, text = stringResource(R.string.new_wallet_short_button_finished))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
    }
}
