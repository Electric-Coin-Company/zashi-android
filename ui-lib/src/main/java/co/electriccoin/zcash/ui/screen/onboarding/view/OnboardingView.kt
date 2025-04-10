@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

// TODO [#1001]: Screens in landscape mode
// TODO [#1001]: https://github.com/Electric-Coin-Company/zashi-android/issues/1001
@PreviewScreens
@Composable
private fun OnboardingComposablePreview() {
    ZcashTheme {
        Onboarding(
            onImportWallet = {},
            onCreateWallet = {}
        )
    }
}

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
fun Onboarding(
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier =
                Modifier.background(
                    if (isSystemInDarkTheme()) {
                        @Suppress("MagicNumber") // does not make sense to suppress this
                        Brush.verticalGradient(
                            .0f to ZashiColors.Surfaces.bgSecondary,
                            .5f to ZashiColors.Surfaces.bgTertiary,
                            0.75f to ZashiColors.Surfaces.bgPrimary,
                        )
                    } else {
                        @Suppress("MagicNumber") // does not make sense to suppress this
                        Brush.verticalGradient(
                            .0f to ZashiColors.Surfaces.bgSecondary,
                            .5f to ZashiColors.Surfaces.bgTertiary,
                            0.75f to ZashiColors.Surfaces.bgPrimary,
                        )
                    }
                )
        ) {
            OnboardingMainContent(
                onImportWallet = onImportWallet,
                onCreateWallet = onCreateWallet,
                modifier =
                    Modifier
                        .padding(
                            top = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacing2xl,
                            bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing4xl,
                            start = ZashiDimensions.Spacing.spacing3xl,
                            end = ZashiDimensions.Spacing.spacing3xl
                        )
            )
        }
    }
}

@Composable
private fun OnboardingMainContent(
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
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
        val imageModifier =
            Modifier
                .height(ZcashTheme.dimens.inScreenZcashLogoHeight)
                .width(ZcashTheme.dimens.inScreenZcashLogoWidth)

        Spacer(Modifier.weight(1f))

        Image(
            painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_logo_without_text),
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = stringResource(R.string.zcash_logo_content_description),
            modifier = imageModifier
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        Image(
            modifier = Modifier.width(220.dp),
            painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_text_logo),
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing3xl))

        Text(
            text = stringResource(R.string.onboarding_header, ZcashCurrency.getLocalizedName(LocalContext.current)),
            style = ZashiTypography.textXl,
            textAlign = TextAlign.Center,
            color = ZashiColors.Text.textSecondary
        )

        @Suppress("MagicNumber") // does not make sense to suppress this
        Spacer(Modifier.weight(.75f))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.onboarding_import_existing_wallet),
            onClick = onImportWallet,
            colors = ZashiButtonDefaults.tertiaryColors()
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

        ZashiButton(
            onClick = onCreateWallet,
            text = stringResource(R.string.onboarding_create_new_wallet),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
