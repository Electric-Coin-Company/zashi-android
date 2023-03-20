@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.restore.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.model.SeedPhraseValidation
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.SecureScreen
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CHIP_GRID_ROW_SIZE
import co.electriccoin.zcash.ui.design.component.Chip
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.NavigationButton
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.model.ParseResult
import co.electriccoin.zcash.ui.screen.restore.model.RestoreStage
import co.electriccoin.zcash.ui.screen.restore.state.RestoreState
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import co.electriccoin.zcash.ui.screen.restore.state.wordValidation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.coroutines.launch

@Preview("Restore Wallet")
@Composable
fun PreviewRestore() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            RestoreWallet(
                ZcashNetwork.Mainnet,
                restoreState = RestoreState(RestoreStage.Seed),
                completeWordList = persistentHashSetOf(
                    "abandon",
                    "ability",
                    "able",
                    "about",
                    "above",
                    "absent",
                    "absorb",
                    "abstract",
                    "rib",
                    "ribbon"
                ),
                userWordList = WordList(listOf("abandon", "absorb")),
                restoreHeight = null,
                setRestoreHeight = {},
                onBack = {},
                paste = { "" },
                onFinished = {}
            )
        }
    }
}

@Preview("Restore Complete")
@Composable
fun PreviewRestoreComplete() {
    ZcashTheme(darkTheme = true) {
        RestoreComplete(
            onComplete = {}
        )
    }
}

// TODO [#409]: https://github.com/zcash/secant-android-wallet/issues/409
/**
 * Note that the restore review doesn't allow the user to go back once the seed is entered correctly.
 *
 * @param restoreHeight A null height indicates no user input.
 */
@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RestoreWallet(
    zcashNetwork: ZcashNetwork,
    restoreState: RestoreState,
    completeWordList: ImmutableSet<String>,
    userWordList: WordList,
    restoreHeight: BlockHeight?,
    setRestoreHeight: (BlockHeight?) -> Unit,
    onBack: () -> Unit,
    paste: () -> String?,
    onFinished: () -> Unit
) {
    var textState by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val parseResult = ParseResult.new(completeWordList, textState)

    val currentStage = restoreState.current.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            RestoreTopAppBar(
                onBack = {
                    if (currentStage.hasPrevious()) {
                        restoreState.goPrevious()
                    } else {
                        onBack()
                    }
                },
                isShowClear = currentStage == RestoreStage.Seed,
                onClear = { userWordList.set(emptyList()) }
            )
        },
        bottomBar = {
            when (currentStage) {
                RestoreStage.Seed -> {
                    RestoreSeedBottomBar(
                        userWordList = userWordList,
                        parseResult = parseResult,
                        setTextState = { textState = it },
                        focusRequester = focusRequester
                    )
                }
                RestoreStage.Birthday -> {
                    // No content
                }
                RestoreStage.Complete -> {
                    // No content
                }
            }
        },
        content = { paddingValues ->
            when (currentStage) {
                RestoreStage.Seed -> {
                    SecureScreen()

                    RestoreSeedMainContent(
                        userWordList = userWordList,
                        textState = textState,
                        setTextState = { textState = it },
                        focusRequester = focusRequester,
                        parseResult = parseResult,
                        paste = paste,
                        goNext = { restoreState.goNext() },
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    )
                }
                RestoreStage.Birthday -> {
                    RestoreBirthday(
                        zcashNetwork = zcashNetwork,
                        initialRestoreHeight = restoreHeight,
                        setRestoreHeight = setRestoreHeight,
                        onNext = { restoreState.goNext() },
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    )
                }
                RestoreStage.Complete -> {
                    // In some cases we need to hide the software keyboard manually, as it stays shown after
                    // input on prior screens
                    LocalSoftwareKeyboardController.current?.hide()

                    RestoreComplete(
                        onComplete = onFinished,
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    )
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RestoreTopAppBar(onBack: () -> Unit, isShowClear: Boolean, onClear: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.restore_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.restore_back_content_description)
                )
            }
        },
        actions = {
            if (isShowClear) {
                NavigationButton(onClick = onClear, stringResource(R.string.restore_button_clear))
            }
        }
    )
}

// TODO [#672] Implement custom seed phrase pasting for wallet import
// TODO [#672] https://github.com/zcash/secant-android-wallet/issues/672

@OptIn(ExperimentalComposeUiApi::class)
@Suppress("UNUSED_PARAMETER", "LongParameterList")
@Composable
private fun RestoreSeedMainContent(
    userWordList: WordList,
    textState: String,
    setTextState: (String) -> Unit,
    focusRequester: FocusRequester,
    parseResult: ParseResult,
    paste: () -> String?,
    goNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUserWordList = userWordList.current.collectAsStateWithLifecycle().value
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    if (parseResult is ParseResult.Add) {
        setTextState("")
        userWordList.append(parseResult.words)
    }

    val isSeedValid = userWordList.wordValidation().collectAsState(null).value is SeedPhraseValidation.Valid

    Column(
        modifier.then(Modifier.verticalScroll(scrollState))
    ) {
        Body(
            modifier = Modifier.padding(dimens.spacingDefault),
            text = stringResource(id = R.string.restore_seed_instructions)
        )

        ChipGridWithText(currentUserWordList)

        if (!isSeedValid) {
            NextWordTextField(
                parseResult = parseResult,
                text = textState,
                setText = { setTextState(it) },
                modifier = Modifier.focusRequester(focusRequester)
            )
        }

        // TODO: Push the button to the bottom of the screen
        Spacer(modifier = Modifier.weight(MINIMAL_WEIGHT))

        PrimaryButton(
            onClick = goNext,
            text = stringResource(id = R.string.restore_seed_button_restore),
            enabled = isSeedValid
        )

        if (isSeedValid) {
            // Hides the keyboard, making it easier for users to see the next button
            LocalSoftwareKeyboardController.current?.hide()
        }
    }

    // Cause text field to refocus
    DisposableEffect(parseResult) {
        if (!isSeedValid) {
            focusRequester.requestFocus()
        }
        scope.launch {
            scrollState.scrollTo(scrollState.maxValue)
        }
        onDispose { }
    }
}

@Composable
private fun RestoreSeedBottomBar(
    userWordList: WordList,
    parseResult: ParseResult,
    setTextState: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val isSeedValid = userWordList.wordValidation().collectAsState(null).value is SeedPhraseValidation.Valid
    // Hide the field once the user has completed the seed phrase; if they need the field back then
    // the user can hit the clear button
    if (!isSeedValid) {
        Column(modifier) {
            Warn(parseResult)
            Autocomplete(parseResult = parseResult, {
                setTextState("")
                userWordList.append(listOf(it))
                focusRequester.requestFocus()
            })
        }
    }
}

@Composable
private fun ChipGridWithText(
    userWordList: ImmutableList<String>
) {
    Column(
        Modifier
            .padding(start = 12.dp, end = 12.dp)
            .testTag(RestoreTag.CHIP_LAYOUT)
    ) {
        userWordList.chunked(CHIP_GRID_ROW_SIZE).forEachIndexed { chunkIndex, chunk ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
                val remainder = (chunk.size % CHIP_GRID_ROW_SIZE)

                val singleItemWeight = 1f / CHIP_GRID_ROW_SIZE
                chunk.forEachIndexed { subIndex, word ->
                    Chip(
                        index = Index(chunkIndex * CHIP_GRID_ROW_SIZE + subIndex),
                        text = word,
                        modifier = Modifier.weight(singleItemWeight)
                    )
                }

                if (0 != remainder) {
                    Spacer(modifier = Modifier.weight((CHIP_GRID_ROW_SIZE - chunk.size) * singleItemWeight))
                }
            }
        }
    }
}

// TODO [#288]: TextField component can't do long-press backspace.
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NextWordTextField(
    parseResult: ParseResult,
    text: String,
    setText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimens.spacingTiny),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 8.dp
    ) {
        /*
         * Treat the user input as a password for more secure input, but disable the transformation
         * to obscure typing.
         */
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spacingTiny)
                .testTag(RestoreTag.SEED_WORD_TEXT_FIELD),
            value = text,
            onValueChange = setText,
            keyboardOptions = KeyboardOptions(
                KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onAny = {}),
            shape = RoundedCornerShape(8.dp),
            isError = parseResult is ParseResult.Warn,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun Autocomplete(
    parseResult: ParseResult,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (isHighlight, suggestions) = when (parseResult) {
        is ParseResult.Autocomplete -> {
            Pair(false, parseResult.suggestions)
        }

        is ParseResult.Warn -> {
            return
        }

        else -> {
            Pair(false, null)
        }
    }
    suggestions?.let {
        val highlightModifier = if (isHighlight) {
            modifier.border(2.dp, ZcashTheme.colors.highlight)
        } else {
            modifier
        }

        @Suppress("ModifierReused")
        LazyRow(highlightModifier.testTag(RestoreTag.AUTOCOMPLETE_LAYOUT)) {
            items(it) {
                Chip(
                    text = it,
                    modifier = modifier
                        .testTag(RestoreTag.AUTOCOMPLETE_ITEM)
                        .clickable { onSuggestionSelected(it) }
                )
            }
        }
    }
}

@Composable
private fun Warn(parseResult: ParseResult) {
    if (parseResult is ParseResult.Warn) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spacingTiny),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondary,
            shadowElevation = 4.dp
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimens.spacingTiny),
                textAlign = TextAlign.Center,
                text = if (parseResult.suggestions.isEmpty()) {
                    stringResource(id = R.string.restore_seed_warning_no_suggestions)
                } else {
                    stringResource(id = R.string.restore_seed_warning_suggestions)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RestoreBirthday(
    zcashNetwork: ZcashNetwork,
    initialRestoreHeight: BlockHeight?,
    setRestoreHeight: (BlockHeight?) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (height, setHeight) = rememberSaveable {
        mutableStateOf(initialRestoreHeight?.value?.toString() ?: "")
    }

    Column(modifier) {
        Header(stringResource(R.string.restore_birthday_header))
        Body(stringResource(R.string.restore_birthday_body))
        TextField(
            value = height,
            onValueChange = { heightString ->
                val filteredHeightString = heightString.filter { it.isDigit() }
                setHeight(filteredHeightString)
            },
            Modifier
                .fillMaxWidth()
                .padding(dimens.spacingTiny)
                .testTag(RestoreTag.BIRTHDAY_TEXT_FIELD),
            label = { Text(stringResource(id = R.string.restore_birthday_hint)) },
            keyboardOptions = KeyboardOptions(
                KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onAny = {}),
            shape = RoundedCornerShape(8.dp),
        )
        Spacer(
            modifier = Modifier
                .weight(MINIMAL_WEIGHT)
        )

        val isBirthdayValid = height.toLongOrNull()?.let {
            it >= zcashNetwork.saplingActivationHeight.value
        } ?: false

        PrimaryButton(
            onClick = {
                setRestoreHeight(BlockHeight.new(zcashNetwork, height.toLong()))
                onNext()
            },
            text = stringResource(R.string.restore_birthday_button_restore),
            enabled = isBirthdayValid
        )
        TertiaryButton(
            onClick = {
                setRestoreHeight(null)
                onNext()
            },
            text = stringResource(R.string.restore_birthday_button_skip)
        )
    }
}

@Composable
private fun RestoreComplete(onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        Header(stringResource(R.string.restore_complete_header))
        Body(stringResource(R.string.restore_complete_info))
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )
        PrimaryButton(onComplete, stringResource(R.string.restore_button_see_wallet))
    }
}
