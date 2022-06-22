package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import cash.z.ecc.sdk.model.PersistableWallet
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CHIP_GRID_ROW_SIZE
import co.electriccoin.zcash.ui.design.component.Chip
import co.electriccoin.zcash.ui.design.component.ChipGrid
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.NavigationButton
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices

@Preview(device = Devices.PIXEL_4)
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            BackupWallet(
                PersistableWalletFixture.new(),
                BackupState(BackupStage.EducationOverview),
                TestChoicesFixture.new(mutableMapOf()),
                onCopyToClipboard = {},
                onComplete = {},
                onChoicesChanged = {}
            )
        }
    }
}

/**
 * @param onComplete Callback when the user has completed the backup test.
 */
@Composable
@Suppress("LongParameterList")
fun BackupWallet(
    wallet: PersistableWallet,
    backupState: BackupState,
    choices: TestChoices,
    onCopyToClipboard: () -> Unit,
    onComplete: () -> Unit,
    onChoicesChanged: ((choicesCount: Int) -> Unit)?
) {
    Column {
        when (backupState.current.collectAsState().value) {
            BackupStage.EducationOverview -> EducationOverview(onNext = backupState::goNext)
            BackupStage.EducationRecoveryPhrase -> EducationRecoveryPhrase(onNext = backupState::goNext)
            BackupStage.Seed -> SeedPhrase(
                wallet,
                onNext = backupState::goNext,
                onCopyToClipboard = onCopyToClipboard
            )
            BackupStage.Test -> Test(
                wallet,
                choices,
                onBack = backupState::goPrevious,
                onNext = backupState::goNext,
                onChoicesChanged = onChoicesChanged
            )
            BackupStage.Complete -> Complete(
                onComplete = onComplete,
                onBackToSeedPhrase = backupState::goToSeed
            )
        }
    }
}

@Composable
private fun EducationOverview(onNext: () -> Unit) {
    Column {
        Header(stringResource(R.string.new_wallet_1_header))
        Body(stringResource(R.string.new_wallet_1_body_1))
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT, true)
        )
        Body(stringResource(R.string.new_wallet_1_body_2))
        PrimaryButton(onClick = onNext, text = stringResource(R.string.new_wallet_1_button))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EducationRecoveryPhrase(onNext: () -> Unit) {
    Column {
        Header(stringResource(R.string.new_wallet_2_header))
        Body(stringResource(R.string.new_wallet_2_body_1))
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT, true)
        )
        Body(stringResource(R.string.new_wallet_2_body_2))
        Card {
            Body(stringResource(R.string.new_wallet_2_body_3))
        }
        PrimaryButton(onClick = onNext, text = stringResource(R.string.new_wallet_2_button))
    }
}

@Composable
private fun SeedPhrase(persistableWallet: PersistableWallet, onNext: () -> Unit, onCopyToClipboard: () -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header(stringResource(R.string.new_wallet_3_header))
        Body(stringResource(R.string.new_wallet_3_body_1))

        ChipGrid(persistableWallet.seedPhrase.split)

        PrimaryButton(onClick = onNext, text = stringResource(R.string.new_wallet_3_button_finished))
        TertiaryButton(onClick = onCopyToClipboard, text = stringResource(R.string.new_wallet_3_button_copy))
    }
}

@Suppress("MagicNumber")
private val testIndices = listOf(Index(4), Index(9), Index(16), Index(20))

private data class TestChoice(val originalIndex: Index, val word: String)

@Composable
private fun Test(
    wallet: PersistableWallet,
    selectedTestChoices: TestChoices,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onChoicesChanged: ((choicesCount: Int) -> Unit)?
) {
    val splitSeedPhrase = wallet.seedPhrase.split

    val currentSelectedTestChoice = selectedTestChoices.current.collectAsState().value

    when {
        currentSelectedTestChoice.size != testIndices.size -> {
            TestInProgress(splitSeedPhrase, selectedTestChoices, onBack, onChoicesChanged)
        }
        currentSelectedTestChoice.all { splitSeedPhrase[it.key.value] == it.value } -> {
            // The user got the test correct
            onNext()
        }
        currentSelectedTestChoice.none { null == it.value } -> {
            TestFailure {
                // Clear the user's prior test inputs for the retest
                selectedTestChoices.set(emptyMap())
                onBack()
            }
        }
    }
}

/*
 * A few implementation notes on the test:
 *  - It is possible for the same word to appear twice in the word choices
 *  - The test answer ordering is not randomized, to ensure it can never be in the correct order to start with
 */
@Composable
private fun TestInProgress(
    splitSeedPhrase: List<String>,
    selectedTestChoices: TestChoices,
    onBack: () -> Unit,
    onChoicesChanged: ((choicesCount: Int) -> Unit)?
) {
    val testChoices = splitSeedPhrase
        .mapIndexed { index, word -> TestChoice(Index(index), word) }
        .filter { testIndices.contains(it.originalIndex) }
        .let {
            // Don't randomize; otherwise there's a chance they'll be in the right order to start with.
            @Suppress("MagicNumber")
            listOf(it[1], it[0], it[3], it[2])
        }
    val currentSelectedTestChoice = selectedTestChoices.current.collectAsState().value
    Column {
        // This button doesn't match the design; just providing the navigation hook for now
        NavigationButton(onClick = onBack, text = stringResource(R.string.new_wallet_4_button_back))

        Header(stringResource(R.string.new_wallet_4_header_verify))
        // Body(stringResource(R.string.new_wallet_4_body_verify))

        Column {
            splitSeedPhrase.chunked(CHIP_GRID_ROW_SIZE).forEachIndexed { chunkIndex, chunk ->
                Row(Modifier.fillMaxWidth()) {
                    chunk.forEachIndexed { subIndex, word ->
                        val currentIndex = Index(chunkIndex * CHIP_GRID_ROW_SIZE + subIndex)

                        if (testIndices.contains(currentIndex)) {
                            ChipDropDown(
                                currentIndex,
                                dropdownText = currentSelectedTestChoice[currentIndex]
                                    ?: "",
                                choices = testChoices.map { it.word },
                                modifier = Modifier
                                    .weight(MINIMAL_WEIGHT)
                                    .testTag(BackupTag.DROPDOWN_CHIP)
                            ) {
                                selectedTestChoices.set(
                                    HashMap(currentSelectedTestChoice).apply {
                                        this[currentIndex] = testChoices[it.value].word
                                    }
                                )
                                if (onChoicesChanged != null) {
                                    onChoicesChanged(selectedTestChoices.current.value.size)
                                }
                            }
                        } else {
                            Chip(
                                index = Index(chunkIndex * CHIP_GRID_ROW_SIZE + subIndex),
                                text = word,
                                modifier = Modifier.weight(MINIMAL_WEIGHT)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestFailure(onBackToSeedPhrase: () -> Unit) {
    Column {
        // This button doesn't match the design; just providing the navigation hook for now
        NavigationButton(onClick = onBackToSeedPhrase, text = stringResource(R.string.new_wallet_4_button_back))

        Header(stringResource(R.string.new_wallet_4_header_ouch))

        Box(Modifier.fillMaxHeight(MINIMAL_WEIGHT))

        Body(stringResource(R.string.new_wallet_4_body_ouch_retry))

        PrimaryButton(onClick = onBackToSeedPhrase, text = stringResource(R.string.new_wallet_4_button_retry))
    }
}

@Composable
private fun Complete(onComplete: () -> Unit, onBackToSeedPhrase: () -> Unit) {
    Column {
        Header(stringResource(R.string.new_wallet_5_header))
        Body(stringResource(R.string.new_wallet_5_body))
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT, true)
        )
        PrimaryButton(onClick = onComplete, text = stringResource(R.string.new_wallet_5_button_finished))
        TertiaryButton(onClick = onBackToSeedPhrase, text = stringResource(R.string.new_wallet_5_button_back))
    }
}
