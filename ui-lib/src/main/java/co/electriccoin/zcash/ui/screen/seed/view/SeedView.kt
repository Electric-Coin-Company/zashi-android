package co.electriccoin.zcash.ui.screen.seed.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import cash.z.ecc.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.ChipGrid
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Seed")
@Composable
fun PreviewSeed() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Seed(
                persistableWallet = PersistableWalletFixture.new(),
                onBack = {},
                onCopyToClipboard = {}
            )
        }
    }
}

/*
 * Note we have some things to determine regarding locking of the secrets for persistableWallet
 * (e.g. seed phrase and spending keys) which should require additional authorization to view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Seed(
    persistableWallet: PersistableWallet,
    onBack: () -> Unit,
    onCopyToClipboard: () -> Unit
) {
    Scaffold(topBar = {
        SeedTopAppBar(onBack = onBack)
    }) {
        SeedMainContent(persistableWallet = persistableWallet, onCopyToClipboard = onCopyToClipboard)
    }
}

@Composable
private fun SeedTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.seed_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.seed_back_content_description)
                )
            }
        }
    )
}

@Composable
private fun SeedMainContent(
    persistableWallet: PersistableWallet,
    onCopyToClipboard: () -> Unit
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header(stringResource(R.string.seed_header))
        Body(stringResource(R.string.seed_body))

        ChipGrid(persistableWallet.seedPhrase.split)

        TertiaryButton(onClick = onCopyToClipboard, text = stringResource(R.string.seed_copy))
    }
}
