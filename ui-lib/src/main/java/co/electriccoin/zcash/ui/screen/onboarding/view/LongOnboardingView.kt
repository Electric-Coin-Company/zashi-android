@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.fixture.WalletFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.NavigationButton
import co.electriccoin.zcash.ui.design.component.PinkProgress
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.screen.onboarding.state.OnboardingState

@Preview("LongOnboarding")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            LongOnboarding(
                OnboardingState(OnboardingStage.Wallet),
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
@Suppress("LongParameterList")
fun LongOnboarding(
    onboardingState: OnboardingState,
    isDebugMenuEnabled: Boolean,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: (seed: String) -> Unit
) {
    val currentStage = onboardingState.current.collectAsStateWithLifecycle().value
    Scaffold(
        topBar = {
            OnboardingTopAppBar(onboardingState, isDebugMenuEnabled, onFixtureWallet)
        },
        bottomBar = {
            BottomNav(currentStage, onboardingState::goNext, onCreateWallet, onImportWallet)
        }
    ) { paddingValues ->
        OnboardingMainContent(
            paddingValues,
            onboardingState
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun OnboardingTopAppBar(
    onboardingState: OnboardingState,
    isDebugMenuEnabled: Boolean,
    onFixtureWallet: (String) -> Unit
) {
    val currentStage = onboardingState.current.collectAsStateWithLifecycle().value

    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            if (currentStage.hasPrevious()) {
                IconButton(onboardingState::goPrevious) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.onboarding_back)
                    )
                }
            }
        },
        actions = {
            if (currentStage.hasNext()) {
                NavigationButton(onboardingState::goToEnd, stringResource(R.string.onboarding_skip))
            }

            if (isDebugMenuEnabled) {
                DebugMenu(onFixtureWallet)
            }
        }
    )
}

@Composable
private fun DebugMenu(
    onFixtureWallet: (String) -> Unit
) {
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
fun OnboardingMainContent(
    paddingValues: PaddingValues,
    onboardingState: OnboardingState
) {
    Column(
        Modifier.padding(top = paddingValues.calculateTopPadding())
    ) {
        val onboardingStage = onboardingState.current.collectAsStateWithLifecycle().value

        when (onboardingStage) {
            OnboardingStage.ShieldedByDefault -> ShieldedByDefault(paddingValues)
            OnboardingStage.UnifiedAddresses -> UnifiedAddresses(paddingValues)
            OnboardingStage.More -> More(paddingValues)
            OnboardingStage.Wallet -> Wallet(paddingValues)
        }
    }
}

@Composable
private fun BottomNav(
    currentStage: OnboardingStage,
    onNext: () -> Unit,
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit
) {
    Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
        if (currentStage == OnboardingStage.Wallet) {
            WalletStageBottomNav(onCreateWallet = onCreateWallet, onImportWallet)
        } else {
            OnboardingBottomNav(currentStage = currentStage, onNext = onNext)
        }
    }
}

@Composable
private fun WalletStageBottomNav(
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(onCreateWallet, stringResource(R.string.onboarding_4_create_new_wallet))
        TertiaryButton(
            onImportWallet,
            stringResource(R.string.onboarding_4_import_existing_wallet),
            Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OnboardingBottomNav(
    currentStage: OnboardingStage,
    onNext: () -> Unit
) {
    SecondaryButton(onNext, stringResource(R.string.onboarding_next), Modifier.fillMaxWidth())
    // Converts from index to human numbering
    Body((currentStage.getProgress().current.value + 1).toString())
    PinkProgress(currentStage.getProgress(), Modifier.fillMaxWidth())
}

@Composable
private fun ShieldedByDefault(paddingValues: PaddingValues) {
    Content(
        paddingValues = paddingValues,
        image = painterResource(id = R.drawable.onboarding_1),
        imageContentDescription = stringResource(R.string.onboarding_1_image_content_description),
        headline = stringResource(R.string.onboarding_1_header),
        body = stringResource(R.string.onboarding_1_body)
    )
}

@Composable
private fun UnifiedAddresses(paddingValues: PaddingValues) {
    Content(
        paddingValues = paddingValues,
        image = painterResource(id = R.drawable.onboarding_2),
        imageContentDescription = stringResource(R.string.onboarding_2_image_content_description),
        headline = stringResource(R.string.onboarding_2_header),
        body = stringResource(R.string.onboarding_2_body)
    )
}

@Composable
private fun More(paddingValues: PaddingValues) {
    Content(
        paddingValues = paddingValues,
        image = painterResource(id = R.drawable.onboarding_3),
        imageContentDescription = stringResource(R.string.onboarding_3_image_content_description),
        headline = stringResource(R.string.onboarding_3_header),
        body = stringResource(R.string.onboarding_3_body)
    )
}

@Composable
private fun Wallet(paddingValues: PaddingValues) {
    Column(
        Modifier
            .padding(
                start = ZcashTheme.dimens.spacingDefault,
                end = ZcashTheme.dimens.spacingDefault,
                bottom = paddingValues.calculateBottomPadding()
            )
            .fillMaxWidth()
    ) {
        Header(
            modifier = Modifier.padding(
                top = ZcashTheme.dimens.spacingDefault,
                bottom = ZcashTheme.dimens.spacingSmall
            ),
            text = stringResource(R.string.onboarding_4_header)
        )
        Body(stringResource(R.string.onboarding_4_body))
    }
}

@Suppress("LongParameterList")
@Composable
private fun Content(
    image: Painter,
    imageContentDescription: String?,
    headline: String,
    body: String,
    paddingValues: PaddingValues,
    imageScale: Float = 0.8f
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Column(
            Modifier.padding(
                start = ZcashTheme.dimens.spacingDefault,
                end = ZcashTheme.dimens.spacingDefault,
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            Image(modifier = Modifier.scale(imageScale), painter = image, contentDescription = imageContentDescription)
            Header(headline)
            Body(body)
        }
    }
}

@Composable
fun Callout(imageVector: ImageVector, contentDescription: String) {
    Box(modifier = Modifier.background(ZcashTheme.colors.callout)) {
        Icon(imageVector, contentDescription, tint = ZcashTheme.colors.onCallout)
    }
}
