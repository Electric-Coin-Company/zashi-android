@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import cash.z.ecc.android.sdk.fixture.WalletFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.ScreenHeight
import co.electriccoin.zcash.ui.design.util.screenHeight
import kotlinx.coroutines.delay

@Preview("ShortOnboarding")
@Composable
private fun ShortOnboardingComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            ShortOnboarding(
                showWelcomeAnim = false,
                isDebugMenuEnabled = false,
                onImportWallet = {},
                onCreateWallet = {},
                onFixtureWallet = {}
            )
        }
    }
}

// TODO [#998]: Check and enhance screen dark mode
// TODO [#998]: https://github.com/Electric-Coin-Company/zashi-android/issues/998

// TODO [#1001]: Screens in landscape mode
// TODO [#1001]: https://github.com/Electric-Coin-Company/zashi-android/issues/1001

/**
 * @param showWelcomeAnim Whether the welcome screen growing chart animation should be done or not.
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
fun ShortOnboarding(
    showWelcomeAnim: Boolean,
    isDebugMenuEnabled: Boolean,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: (String) -> Unit
) {
    Scaffold { paddingValues ->
        val screenHeight = screenHeight()
        val (welcomeAnimVisibility, setWelcomeAnimVisibility) =
            rememberSaveable {
                mutableStateOf(showWelcomeAnim)
            }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedImage(
                    screenHeight = screenHeight,
                    welcomeAnimVisibility = welcomeAnimVisibility,
                    setWelcomeAnimVisibility = setWelcomeAnimVisibility,
                    modifier = Modifier.zIndex(1f)
                )
                OnboardingMainContent(
                    isDebugMenuEnabled = isDebugMenuEnabled,
                    onImportWallet = onImportWallet,
                    onCreateWallet = onCreateWallet,
                    onFixtureWallet = onFixtureWallet,
                    modifier =
                        Modifier
                            .padding(
                                top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingHuge,
                                bottom = paddingValues.calculateBottomPadding(),
                                start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                                end = ZcashTheme.dimens.screenHorizontalSpacingBig
                            )
                            .height(screenHeight.contentHeight - paddingValues.calculateBottomPadding())
                )
            }
        }
    }
}

@Composable
private fun DebugMenu(onFixtureWallet: (String) -> Unit) {
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
                text = { Text("Import Alice's wallet") },
                onClick = { onFixtureWallet(WalletFixture.Alice.seedPhrase) }
            )
            DropdownMenuItem(
                text = { Text("Import Ben's wallet") },
                onClick = { onFixtureWallet(WalletFixture.Ben.seedPhrase) }
            )
        }
    }
}

@Composable
private fun OnboardingMainContent(
    isDebugMenuEnabled: Boolean,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    @Suppress("ModifierNotUsedAtRoot")
    Box {
        SmallTopAppBar(
            regularActions = {
                if (isDebugMenuEnabled) {
                    DebugMenu(onFixtureWallet)
                }
            }
        )
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_logo_without_text),
                stringResource(R.string.zcash_logo_content_description),
                Modifier
                    .height(ZcashTheme.dimens.inScreenZcashLogoHeight)
                    .width(ZcashTheme.dimens.inScreenZcashLogoWidth)
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            Image(
                painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_text_logo),
                ""
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

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

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

            SecondaryButton(
                onImportWallet,
                stringResource(R.string.onboarding_import_existing_wallet)
            )
        }
    }
}

@Composable
fun AnimatedImage(
    screenHeight: ScreenHeight,
    welcomeAnimVisibility: Boolean,
    setWelcomeAnimVisibility: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO [#1002]: Welcome screen animation masking
    // TODO [#1002]: https://github.com/Electric-Coin-Company/zashi-android/issues/1002

    AnimatedVisibility(
        visible = welcomeAnimVisibility,
        exit =
            slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(AnimationConstants.ANIMATION_DURATION)
            ),
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxHeight()) {
                Image(
                    painter = ColorPainter(ZcashTheme.colors.welcomeAnimationColor),
                    contentScale = ContentScale.FillBounds,
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .height(screenHeight.overallScreenHeight()),
                    contentDescription = null
                )
                Image(
                    painter = painterResource(id = R.drawable.chart_line),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo_with_hi),
                contentDescription = stringResource(R.string.zcash_logo_with_hi_text_content_description),
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(top = screenHeight.systemStatusBarHeight + ZcashTheme.dimens.spacingHuge)
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(AnimationConstants.INITIAL_DELAY)
        setWelcomeAnimVisibility(false)
    }
}

object AnimationConstants {
    const val ANIMATION_DURATION = 1250
    const val INITIAL_DELAY: Long = 800
}
