@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.WalletFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Onboarding")
@Composable
private fun OnboardingComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        Onboarding(
            isDebugMenuEnabled = true,
            onImportWallet = {},
            onCreateWallet = {},
            onFixtureWallet = {}
        )
    }
}

// TODO [#998]: Check and enhance screen dark mode
// TODO [#998]: https://github.com/Electric-Coin-Company/zashi-android/issues/998

// TODO [#1001]: Screens in landscape mode
// TODO [#1001]: https://github.com/Electric-Coin-Company/zashi-android/issues/1001

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
fun Onboarding(
    isDebugMenuEnabled: Boolean,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: (String) -> Unit
) {
    GridBgScaffold { paddingValues ->
        OnboardingMainContent(
            isDebugMenuEnabled = isDebugMenuEnabled,
            onCreateWallet = onCreateWallet,
            onFixtureWallet = onFixtureWallet,
            onImportWallet = onImportWallet,
            modifier =
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingHuge,
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    )
        )
    }
}

@Composable
private fun OnboardingMainContent(
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: (String) -> Unit,
    isDebugMenuEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var imageModifier =
            Modifier
                .height(ZcashTheme.dimens.inScreenZcashLogoHeight)
                .width(ZcashTheme.dimens.inScreenZcashLogoWidth)
        if (isDebugMenuEnabled) {
            imageModifier =
                imageModifier.then(
                    Modifier.clickable {
                        onFixtureWallet(WalletFixture.Alice.seedPhrase)
                    }
                )
        }

        Image(
            painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_logo_without_text),
            stringResource(R.string.zcash_logo_content_description),
            modifier = imageModifier
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Image(
            painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_text_logo),
            ""
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        TitleLarge(text = stringResource(R.string.onboarding_header), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            onClick = onCreateWallet,
            text = stringResource(R.string.onboarding_create_new_wallet),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        SecondaryButton(
            onImportWallet,
            stringResource(R.string.onboarding_import_existing_wallet)
        )
    }
}
