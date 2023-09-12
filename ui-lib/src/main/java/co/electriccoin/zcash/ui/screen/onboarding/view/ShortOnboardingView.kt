@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Preview("ShortOnboarding")
@Composable
private fun ShortOnboardingComposablePreview() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            ShortOnboarding(
                onImportWallet = {},
                onCreateWallet = {},
            )
        }
    }
}

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
fun ShortOnboarding(
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
) {
    Scaffold { paddingValues ->
        val (screenHeight) = getScreenHeight()
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState(), enabled = true)
        ) {
            Box(modifier = Modifier.fillMaxHeight()) {
                AnimatedImage(Modifier.zIndex(1f))
                OnboardingMainContent(
                    onImportWallet = onImportWallet,
                    onCreateWallet = onCreateWallet,
                    modifier = Modifier
                        .padding(
                            top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingHuge,
                            bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                            start = ZcashTheme.dimens.spacingHuge,
                            end = ZcashTheme.dimens.spacingHuge
                        )
                        .fillMaxHeight()
                        .height(
                            screenHeight.dp - (paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge)
                        )
                        .zIndex(-1f)
                )
            }
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
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(id = R.drawable.zashi_logo_without_text),
            stringResource(R.string.zcash_logo_onboarding_content_description),
            Modifier
                .height(ZcashTheme.dimens.zcashLogoHeight)
                .width(ZcashTheme.dimens.zcashLogoWidth)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Image(
            painterResource(id = R.drawable.zashi_text_logo),
            ""
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        TitleLarge(text = stringResource(R.string.onboarding_short_header), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            onClick = onCreateWallet,
            text = stringResource(R.string.onboarding_short_create_new_wallet),
            outerPaddingValues = PaddingValues(
                horizontal = ZcashTheme.dimens.spacingNone,
                vertical = ZcashTheme.dimens.spacingSmall
            ),
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onImportWallet,
            stringResource(R.string.onboarding_short_import_existing_wallet),
            outerPaddingValues = PaddingValues(
                horizontal = ZcashTheme.dimens.spacingNone,
                vertical = ZcashTheme.dimens.spacingSmall
            )
        )
    }
}

@Composable
fun getScreenHeight(): Array<Int> {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
    val extraDownPx = with(density) { ZcashTheme.dimens.extraHeightBeyondScreen.roundToPx() }
    val screenHeight = screenHeightPx / LocalDensity.current.density.roundToInt()
    val extraHeight = extraDownPx / LocalDensity.current.density.roundToInt()
    return arrayOf(screenHeight, extraHeight)
}

@Composable
fun AnimatedImage(modifier: Modifier = Modifier) {
    var visible by remember {
        mutableStateOf(true)
    }

    val (screenHeight, extraHeight) = getScreenHeight()

    AnimatedVisibility(
        visible = visible,
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(AnimationConstants.ANIMATION_DURATION))
    ) {
        Box() {
            Image(
                painter = painterResource(id = R.drawable.cracked_corner_screen),
                contentDescription = stringResource(R.string.cracked_corner_image_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height((screenHeight + extraHeight).dp)
                    .fillMaxWidth()
                    .then(modifier)
            )
            Image(
                painter = painterResource(id = R.drawable.logo_with_hi),
                contentDescription = stringResource(R.string.zcash_logo_with_hi_text_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ZcashTheme.dimens.spacingHuge)
                    .then(modifier)
            )
        }
    }
    LaunchedEffect(Unit) {
        delay(AnimationConstants.INITIAL_DELAY)
        visible = false
    }
}

object AnimationConstants {
    const val ANIMATION_DURATION = 2500
    const val INITIAL_DELAY: Long = 100
}
