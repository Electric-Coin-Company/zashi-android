@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
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
import co.electriccoin.zcash.spackle.model.Progress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
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
                OnboardingState(OnboardingStage.UnifiedAddresses),
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
    Scaffold(
        topBar = {
            OnboardingTopAppBar(onboardingState, isDebugMenuEnabled, onFixtureWallet)
        }
    ) { paddingValues ->
        OnboardingMainContent(
            paddingValues,
            onboardingState,
            onImportWallet = onImportWallet,
            onCreateWallet = onCreateWallet
        )
    }
}

@Composable
private fun OnboardingTopAppBar(
    onboardingState: OnboardingState,
    isDebugMenuEnabled: Boolean,
    onFixtureWallet: () -> Unit
) {
    val currentStage = onboardingState.current.collectAsState().value

    SmallTopAppBar(
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
    onboardingState: OnboardingState,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit
) {
    Column(
        Modifier
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        if (!IS_NAVIGATION_IN_APP_BAR) {
            TopNavButtons(onboardingState)
        }

        val onboardingStage = onboardingState.current.collectAsState().value

        when (onboardingStage) {
            OnboardingStage.ShieldedByDefault -> ShieldedByDefault()
            OnboardingStage.UnifiedAddresses -> UnifiedAddresses()
            OnboardingStage.More -> More()
            OnboardingStage.Wallet -> Wallet(
                onCreateWallet = onCreateWallet,
                onImportWallet = onImportWallet
            )
        }

        BottomNav(onboardingStage.getProgress(), onboardingState::goNext)
    }
}

@Composable
private fun TopNavButtons(onboardingState: OnboardingState) {
    Row {
        val currentStage = onboardingState.current.collectAsState().value
        if (currentStage.hasPrevious()) {
            NavigationButton(onboardingState::goPrevious, stringResource(R.string.onboarding_back))
        }

        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT, true)
        )

        if (currentStage.hasNext()) {
            NavigationButton(onboardingState::goToEnd, stringResource(R.string.onboarding_skip))
        }
    }
}

@Composable
private fun BottomNav(progress: Progress, onNext: () -> Unit) {
    if (progress.current != progress.last) {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT, true)
            )

            SecondaryButton(onNext, stringResource(R.string.onboarding_next), Modifier.fillMaxWidth())

            // Converts from index to human numbering
            Body((progress.current.value + 1).toString())

            PinkProgress(progress, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ShieldedByDefault() {
    Column {
        Content(
            image = painterResource(id = R.drawable.onboarding_1_shielded),
            imageContentDescription = stringResource(R.string.onboarding_1_image_content_description),
            headline = stringResource(R.string.onboarding_1_header),
            body = stringResource(R.string.onboarding_1_body)
        )
    }
}

@Composable
private fun UnifiedAddresses() {
    Column {
        Content(
            image = painterResource(id = R.drawable.onboarding_2_unified),
            imageContentDescription = stringResource(R.string.onboarding_2_image_content_description),
            headline = stringResource(R.string.onboarding_2_header),
            body = stringResource(R.string.onboarding_2_body)
        )
    }
}

@Composable
private fun More() {
    Column {
        Content(
            image = painterResource(id = R.drawable.onboarding_3_more),
            imageContentDescription = stringResource(R.string.onboarding_3_image_content_description),
            headline = stringResource(R.string.onboarding_3_header),
            body = stringResource(R.string.onboarding_3_body)
        )
    }
}

@Composable
private fun Wallet(onCreateWallet: () -> Unit, onImportWallet: () -> Unit) {
    Column {
        Header(stringResource(R.string.onboarding_4_header))
        Body(stringResource(R.string.onboarding_4_body))
        PrimaryButton(onCreateWallet, stringResource(R.string.onboarding_4_create_new_wallet), Modifier.fillMaxWidth())
        TertiaryButton(
            onImportWallet,
            stringResource(R.string.onboarding_4_import_existing_wallet),
            Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Content(
    image: Painter,
    imageContentDescription: String?,
    headline: String,
    body: String
) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            // TODO [#17]: This suppression and magic number will get replaced once we have real assets
            @Suppress("MagicNumber")
            Image(image, imageContentDescription, Modifier.fillMaxSize(0.50f))
        }
        Header(headline)
        Body(body)
    }
}

@Composable
fun Callout(imageVector: ImageVector, contentDescription: String) {
    Box(modifier = Modifier.background(ZcashTheme.colors.callout)) {
        Icon(imageVector, contentDescription, tint = ZcashTheme.colors.onCallout)
    }
}
