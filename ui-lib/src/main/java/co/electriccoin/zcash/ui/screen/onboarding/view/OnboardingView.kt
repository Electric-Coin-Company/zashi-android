@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Onboarding(
                OnboardingState(OnboardingStage.Wallet),
                false,
                onImportWallet = {},
                onCreateWallet = {},
                onFixtureWallet = {}
            )
        }
    }
}

// Pending internal discussion on where the navigation should live.
// This toggles between the app bar or custom buttons below the app bar
private const val IS_NAVIGATION_IN_APP_BAR = false

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Onboarding(
    onboardingState: OnboardingState,
    isDebugMenuEnabled: Boolean,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit,
    onFixtureWallet: () -> Unit
) {
    val currentStage = onboardingState.current.collectAsState().value
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
    onFixtureWallet: () -> Unit
) {
    val currentStage = onboardingState.current.collectAsState().value

    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon =
        if (IS_NAVIGATION_IN_APP_BAR && currentStage.hasPrevious()) {
            {
                IconButton(onboardingState::goPrevious) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.onboarding_back)
                    )
                }
            }
        } else {
            { Unit }
        },
        actions = {
            if (IS_NAVIGATION_IN_APP_BAR && currentStage.hasNext()) {
                NavigationButton(onboardingState::goToEnd, stringResource(R.string.onboarding_skip))
            }

            if (isDebugMenuEnabled) {
                DebugMenu(onFixtureWallet)
            }
        }
    )
}

@Composable
private fun DebugMenu(onFixtureWallet: () -> Unit) {
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

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
fun OnboardingMainContent(
    paddingValues: PaddingValues,
    onboardingState: OnboardingState
) {
    Column(
        Modifier.padding(top = paddingValues.calculateTopPadding())
    ) {
        if (!IS_NAVIGATION_IN_APP_BAR) {
            TopNavButtons(onboardingState)
        }

        val onboardingStage = onboardingState.current.collectAsState().value

        when (onboardingStage) {
            OnboardingStage.ShieldedByDefault -> ShieldedByDefault(paddingValues)
            OnboardingStage.UnifiedAddresses -> UnifiedAddresses(paddingValues)
            OnboardingStage.More -> More(paddingValues)
            OnboardingStage.Wallet -> Wallet(paddingValues)
        }
    }
}

@Composable
private fun TopNavButtons(onboardingState: OnboardingState) {
    val currentStage = onboardingState.current.collectAsState().value
    if (currentStage == OnboardingStage.ShieldedByDefault) return
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        if (currentStage.hasPrevious()) {
            NavigationButton(onboardingState::goPrevious, stringResource(R.string.onboarding_back))
        }
        if (currentStage.hasNext()) {
            NavigationButton(onboardingState::goToEnd, stringResource(R.string.onboarding_skip))
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
    Column {
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
    PrimaryButton(onCreateWallet, stringResource(R.string.onboarding_4_create_new_wallet), Modifier.fillMaxWidth())
    TertiaryButton(
        onImportWallet,
        stringResource(R.string.onboarding_4_import_existing_wallet),
        Modifier.fillMaxWidth()
    )
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
                start = ZcashTheme.paddings.padding,
                end = ZcashTheme.paddings.padding,
                bottom = paddingValues.calculateBottomPadding()
            )
            .fillMaxWidth()
    ) {
        Header(
            modifier = Modifier.padding(
                top = ZcashTheme.paddings.padding,
                bottom = ZcashTheme.paddings.paddingHalf
            ),
            text = stringResource(R.string.onboarding_4_header)
        )
        Body(stringResource(R.string.onboarding_4_body))
        Image(painterResource(id = R.drawable.onboarding_4), "")
    }
}

@Composable
private fun Content(
    image: Painter,
    imageContentDescription: String?,
    headline: String,
    body: String,
    paddingValues: PaddingValues
) {
    val scrollState = rememberScrollState()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(ZcashTheme.paddings.padding)
            .verticalScroll(scrollState)
    ) {
        Column(Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            Image(image, imageContentDescription)
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
