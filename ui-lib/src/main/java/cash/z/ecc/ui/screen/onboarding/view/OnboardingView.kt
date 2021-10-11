package cash.z.ecc.ui.screen.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.ui.R
import cash.z.ecc.ui.screen.onboarding.model.OnboardingStage
import cash.z.ecc.ui.screen.onboarding.model.Progress
import cash.z.ecc.ui.screen.onboarding.state.OnboardingState
import cash.z.ecc.ui.theme.MINIMAL_WEIGHT

@Preview
@Composable
fun ComposablePreview() {
    Onboarding(
        OnboardingState(OnboardingStage.UnifiedAddresses),
        onImportWallet = {},
        onCreateWallet = {}
    )
}

/**
 * @param onImportWallet Callback when the user decides to import an existing wallet.
 * @param onCreateWallet Callback when the user decides to create a new wallet.
 */
@Composable
fun Onboarding(
    onboardingState: OnboardingState,
    onImportWallet: () -> Unit,
    onCreateWallet: () -> Unit
) {
    Column {
        TopNavButtons(onboardingState)

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
        if (onboardingState.hasPrevious()) {
            Button(onboardingState::goPrevious) {
                Text(stringResource(R.string.onboarding_back))
            }
        }

        Spacer(Modifier.fillMaxWidth().weight(MINIMAL_WEIGHT, true))

        if (onboardingState.hasNext()) {
            Button(onboardingState::goToEnd) {
                Text(stringResource(R.string.onboarding_skip))
            }
        }
    }
}

@Composable
private fun BottomNav(progress: Progress, onNext: () -> Unit) {
    if (progress.current != progress.last) {
        Column {
            Button(onNext, Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.onboarding_next))
            }

            // Converts from index to human numbering
            Text((progress.current.value + 1).toString())

            LinearProgressIndicator(progress = progress.percent().decimal, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ShieldedByDefault() {
    Column {
        Content(
            image = ColorPainter(Color.Blue),
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
            image = ColorPainter(Color.Blue),
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
            image = ColorPainter(Color.Blue),
            imageContentDescription = stringResource(R.string.onboarding_3_image_content_description),
            headline = stringResource(R.string.onboarding_3_header),
            body = stringResource(R.string.onboarding_3_body)
        )
    }
}

@Composable
private fun Wallet(onCreateWallet: () -> Unit, onImportWallet: () -> Unit) {
    Column {
        Button(onCreateWallet, Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.onboarding_4_create_new_wallet))
        }
        Button(onImportWallet, Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.onboarding_4_import_existing_wallet))
        }
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
        Text(headline)
        Text(body)
    }
}
