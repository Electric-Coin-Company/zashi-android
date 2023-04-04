@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.backup.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.SecureScreen
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CHIP_GRID_ROW_SIZE
import co.electriccoin.zcash.ui.design.component.Chip
import co.electriccoin.zcash.ui.design.component.ChipGrid
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Preview(device = Devices.PIXEL_4)
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            LongNewWalletBackup(
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList")
fun LongNewWalletBackup(
    wallet: PersistableWallet,
    backupState: BackupState,
    choices: TestChoices,
    onCopyToClipboard: () -> Unit,
    onComplete: () -> Unit,
    onChoicesChanged: ((choicesCount: Int) -> Unit)?
) {
    val currentBackupStage = backupState.current.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            BackupTopAppBar(
                backupStage = currentBackupStage,
                onCopyToClipboard = onCopyToClipboard,
                onBack = backupState::goPrevious,
                selectedTestChoices = choices
            )
        },
        bottomBar = {
            BackupBottomNav(
                backupStage = currentBackupStage,
                onNext = backupState::goNext,
                onBack = backupState::goPrevious,
                selectedTestChoices = choices,
                onComplete = onComplete,
                onBackToSeedPhrase = {
                    backupState.goToStage(BackupStage.ReviewSeed)
                }
            )
        }
    ) { paddingValues ->
        BackupMainContent(
            paddingValues = paddingValues,
            backupState = backupState,
            wallet = wallet,
            choices = choices,
            onChoicesChanged = onChoicesChanged
        )
    }
}

@Composable
fun BackupMainContent(
    paddingValues: PaddingValues,
    backupState: BackupState,
    wallet: PersistableWallet,
    choices: TestChoices,
    onChoicesChanged: ((choicesCount: Int) -> Unit)?
) {
    Column(
        Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        when (backupState.current.collectAsStateWithLifecycle().value) {
            is BackupStage.EducationOverview -> EducationOverview()
            is BackupStage.EducationRecoveryPhrase -> EducationRecoveryPhrase()
            is BackupStage.Seed -> SeedPhrase(wallet)
            is BackupStage.Test -> TestInProgress(
                selectedTestChoices = choices,
                onChoicesChanged = onChoicesChanged,
                splitSeedPhrase = wallet.seedPhrase.split.toPersistentList(),
                backupState = backupState
            )
            is BackupStage.Failure -> TestFailure()
            is BackupStage.Complete -> TestComplete()
            is BackupStage.ReviewSeed -> SeedPhrase(wallet)
        }
    }
}

@Composable
private fun EducationOverview() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Body(stringResource(R.string.new_wallet_1_body_1))
        Image(
            painter = painterResource(id = R.drawable.backup_1),
            contentDescription = stringResource(id = R.string.backup_1_content_description)
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT, true)
        )
        Body(stringResource(R.string.new_wallet_1_body_2))
    }
}

@Composable
private fun EducationRecoveryPhrase() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Body(stringResource(R.string.new_wallet_2_body_1))
        Image(
            painter = painterResource(id = R.drawable.backup_2),
            contentDescription = stringResource(id = R.string.backup_2_content_description)
        )
        Body(stringResource(R.string.new_wallet_2_body_2))
        Card {
            Body(stringResource(R.string.new_wallet_2_body_3))
        }
    }
}

@Composable
private fun SeedPhrase(persistableWallet: PersistableWallet) {
    SecureScreen()
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = ZcashTheme.dimens.spacingDefault)
    ) {
        Body(stringResource(R.string.new_wallet_3_body_1))
        ChipGrid(persistableWallet.seedPhrase.split.toPersistentList())
    }
}

@Suppress("MagicNumber")
private val testIndices = listOf(Index(4), Index(9), Index(16), Index(20))

private data class TestChoice(val originalIndex: Index, val word: String)

/*
 * A few implementation notes on the test:
 *  - It is possible for the same word to appear twice in the word choices
 *  - The test answer ordering is not randomized, to ensure it can never be in the correct order to start with
 */

@Composable
private fun TestInProgress(
    splitSeedPhrase: ImmutableList<String>,
    selectedTestChoices: TestChoices,
    onChoicesChanged: ((choicesCount: Int) -> Unit)?,
    backupState: BackupState
) {
    SecureScreen()

    val testChoices = splitSeedPhrase
        .mapIndexed { index, word -> TestChoice(Index(index), word) }
        .filter { testIndices.contains(it.originalIndex) }
        .let {
            // Don't randomize; otherwise there's a chance they'll be in the right order to start with.
            @Suppress("MagicNumber")
            listOf(it[1], it[0], it[3], it[2])
        }
    val currentSelectedTestChoice = selectedTestChoices.current.collectAsStateWithLifecycle().value
    if (currentSelectedTestChoice.size == testIndices.size) {
        if (currentSelectedTestChoice.all { splitSeedPhrase[it.key.value] == it.value }) {
            // the user got the test correct
            backupState.goNext()
        } else {
            backupState.goToStage(BackupStage.Failure)
        }
    }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = ZcashTheme.dimens.spacingDefault)
    ) {
        splitSeedPhrase.chunked(CHIP_GRID_ROW_SIZE).forEachIndexed { chunkIndex, chunk ->
            Row(Modifier.fillMaxWidth()) {
                chunk.forEachIndexed { subIndex, word ->
                    val currentIndex = Index(chunkIndex * CHIP_GRID_ROW_SIZE + subIndex)

                    if (testIndices.contains(currentIndex)) {
                        ChipDropDown(
                            currentIndex,
                            dropdownText = currentSelectedTestChoice[currentIndex]
                                ?: "",
                            choices = testChoices.map { it.word }.toPersistentList(),
                            {
                                selectedTestChoices.set(
                                    HashMap(currentSelectedTestChoice).apply {
                                        this[currentIndex] = testChoices[it.value].word
                                    }
                                )
                                if (onChoicesChanged != null) {
                                    onChoicesChanged(selectedTestChoices.current.value.size)
                                }
                            },
                            modifier = Modifier
                                .weight(MINIMAL_WEIGHT)
                                .testTag(BackupTag.DROPDOWN_CHIP)
                        )
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

@Composable
private fun TestFailure() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.backup_failure),
            contentDescription = stringResource(id = R.string.backup_failure_content_description)
        )
        Box(Modifier.fillMaxHeight(MINIMAL_WEIGHT))
        Body(stringResource(R.string.new_wallet_4_body_ouch_retry))
    }
}

@Composable
private fun TestComplete() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Body(stringResource(R.string.new_wallet_5_body))
        Image(
            painter = painterResource(id = R.drawable.backup_success),
            contentDescription = stringResource(id = R.string.backup_success_content_description)
        )

        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT, true)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BackupTopAppBar(
    backupStage: BackupStage,
    onCopyToClipboard: () -> Unit,
    onBack: () -> Unit,
    selectedTestChoices: TestChoices
) {
    var showCopySeedMenu = false
    val screenTitleResId = when (backupStage) {
        is BackupStage.EducationOverview -> {
            R.string.new_wallet_1_header
        }
        is BackupStage.EducationRecoveryPhrase -> {
            R.string.new_wallet_2_header
        }
        is BackupStage.Seed -> {
            showCopySeedMenu = true
            R.string.new_wallet_3_header
        }
        is BackupStage.Test -> {
            R.string.new_wallet_4_header
        }
        is BackupStage.Failure -> {
            R.string.new_wallet_4_header_ouch
        }
        is BackupStage.Complete -> {
            R.string.new_wallet_5_header
        }
        is BackupStage.ReviewSeed -> {
            showCopySeedMenu = true
            R.string.new_wallet_3_header
        }
    }

    TopAppBar(
        title = { Text(text = stringResource(id = screenTitleResId)) },
        navigationIcon = {
            // hide back navigation button for the first and Complete stages
            if (backupStage.hasPrevious() && backupStage != BackupStage.Complete) {
                val onBackClickListener = {
                    if (backupStage is BackupStage.Failure) {
                        // Clear the user's prior test inputs for the retest
                        selectedTestChoices.set(emptyMap())
                    }
                    onBack()
                }
                BackHandler(enabled = true) { onBackClickListener() }
                IconButton(onBackClickListener) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(
                            R.string.new_wallet_navigation_back_button_content_description
                        )
                    )
                }
            }
        },
        actions = {
            if (showCopySeedMenu) {
                CopySeedMenu(onCopyToClipboard)
            }
        }
    )
}

@Composable
private fun CopySeedMenu(onCopyToClipboard: () -> Unit) {
    Column {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.new_wallet_toolbar_more_button_content_description)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.new_wallet_3_button_copy)) },
                onClick = {
                    expanded = false
                    onCopyToClipboard()
                }
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun BackupBottomNav(
    backupStage: BackupStage,
    onNext: () -> Unit,
    onBack: () -> Unit,
    selectedTestChoices: TestChoices,
    onComplete: () -> Unit,
    onBackToSeedPhrase: () -> Unit
) {
    Column {
        when (backupStage) {
            is BackupStage.EducationOverview -> {
                PrimaryButton(onClick = onNext, text = stringResource(R.string.new_wallet_1_button))
            }
            is BackupStage.EducationRecoveryPhrase -> {
                PrimaryButton(onClick = onNext, text = stringResource(R.string.new_wallet_2_button))
            }
            is BackupStage.Seed -> {
                PrimaryButton(onClick = onNext, text = stringResource(R.string.new_wallet_3_button_finished))
            }
            is BackupStage.Test -> {
                // no bottom navigation button placed
            }
            is BackupStage.Failure -> {
                PrimaryButton(
                    onClick = {
                        // Clear the user's prior test inputs for the retest
                        selectedTestChoices.set(emptyMap())
                        onBack()
                    },
                    text = stringResource(R.string.new_wallet_4_button_retry)
                )
            }
            is BackupStage.Complete -> {
                PrimaryButton(onClick = onComplete, text = stringResource(R.string.new_wallet_5_button_finished))
                TertiaryButton(onClick = onBackToSeedPhrase, text = stringResource(R.string.new_wallet_5_button_back))
            }
            is BackupStage.ReviewSeed -> {
                PrimaryButton(onClick = onBack, text = stringResource(R.string.new_wallet_3_button_finished))
            }
        }
    }
}
