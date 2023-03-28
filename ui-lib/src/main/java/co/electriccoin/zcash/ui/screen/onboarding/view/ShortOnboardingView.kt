@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun ShortOnboardingComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            ShortOnboarding(
                isDebugMenuEnabled = false,
                onImportWallet = {},
                onCreateWallet = {},
                onFixtureWallet = {}
            )
        }
    }
}

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ShortOnboarding(
    isDebugMenuEnabled: Boolean,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: () -> Unit
) {
    Scaffold(
        topBar = {
            OnboardingTopAppBar(isDebugMenuEnabled, onFixtureWallet)
        }
    ) { paddingValues ->
        OnboardingMainContent(
            onImportWallet = onImportWallet,
            onCreateWallet = onCreateWallet,
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
@OptIn(ExperimentalMaterial3Api::class)
private fun OnboardingTopAppBar(
    isDebugMenuEnabled: Boolean,
    onFixtureWallet: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            if (isDebugMenuEnabled) {
                DebugMenu(onFixtureWallet)
            }
        }
    )
}

@Composable
private fun DebugMenu(onFixtureWallet: () -> Unit) {
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
                text = { Text("Import wallet with fixture seed phrase") },
                onClick = onFixtureWallet
            )
        }
    }
}

@Composable
private fun OnboardingMainContent(
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(
                rememberScrollState()
            )
            .then(modifier)
    ) {
        Header(text = stringResource(R.string.onboarding_short_header))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXlarge))

        Body(text = stringResource(R.string.onboarding_short_information))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(onCreateWallet, stringResource(R.string.onboarding_short_create_new_wallet), Modifier.fillMaxWidth())
        TertiaryButton(
            onImportWallet,
            stringResource(R.string.onboarding_short_import_existing_wallet),
            Modifier.fillMaxWidth()
        )
    }
}
