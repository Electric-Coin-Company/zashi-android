@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.restore.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.model.SeedPhraseValidation
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.SecureScreen
import co.electriccoin.zcash.ui.common.compose.shouldSecureScreen
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.ChipOnSurface
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.GridBgSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Reference
import co.electriccoin.zcash.ui.design.component.TopScreenLogoTitle
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.model.ParseResult
import co.electriccoin.zcash.ui.screen.restore.model.RestoreStage
import co.electriccoin.zcash.ui.screen.restore.state.RestoreState
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import co.electriccoin.zcash.ui.screen.restore.state.wordValidation
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.coroutines.launch

@Preview
@Composable
private fun RestoreSeedPreview() {
    ZcashTheme(forceDarkMode = false) {
        RestoreWallet(
            ZcashNetwork.Mainnet,
            restoreState = RestoreState(RestoreStage.Seed),
            completeWordList =
                persistentHashSetOf(
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

@Preview
@Composable
private fun RestoreSeedDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        RestoreWallet(
            ZcashNetwork.Mainnet,
            restoreState = RestoreState(RestoreStage.Seed),
            completeWordList =
                persistentHashSetOf(
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

@Preview
@Composable
private fun RestoreBirthdayPreview() {
    ZcashTheme(forceDarkMode = false) {
        RestoreWallet(
            ZcashNetwork.Mainnet,
            restoreState = RestoreState(RestoreStage.Birthday),
            completeWordList =
                persistentHashSetOf(
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

@Preview
@Composable
private fun RestoreBirthdayDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        RestoreWallet(
            ZcashNetwork.Mainnet,
            restoreState = RestoreState(RestoreStage.Birthday),
            completeWordList =
                persistentHashSetOf(
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

/**
 * Note that the restore review doesn't allow the user to go back once the seed is entered correctly.
 *
 * @param restoreHeight A null height indicates no user input.
 */
@Suppress("LongParameterList", "LongMethod")
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
    val scope = rememberCoroutineScope()
    var text by rememberSaveable { mutableStateOf("") }
    val parseResult = ParseResult.new(completeWordList, text)

    val currentStage = restoreState.current.collectAsStateWithLifecycle().value

    var isSeedValid by rememberSaveable { mutableStateOf(false) }
    // To avoid unnecessary recompositions that this flow produces
    SideEffect {
        scope.launch {
            userWordList.wordValidation().collect {
                isSeedValid = it is SeedPhraseValidation.Valid
            }
        }
    }

    GridBgScaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            when (currentStage) {
                RestoreStage.Seed -> {
                    RestoreSeedTopAppBar(
                        onBack = onBack,
                        onClear = {
                            userWordList.set(emptyList())
                            text = ""
                        }
                    )
                }
                RestoreStage.Birthday -> {
                    RestoreSeedBirthdayTopAppBar(
                        onBack = {
                            if (currentStage.hasPrevious()) {
                                restoreState.goPrevious()
                            } else {
                                onBack()
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            when (currentStage) {
                RestoreStage.Seed -> {
                    RestoreSeedBottomBar(
                        userWordList = userWordList,
                        isSeedValid = isSeedValid,
                        parseResult = parseResult,
                        setText = { text = it },
                        modifier =
                            Modifier
                                .imePadding()
                                .navigationBarsPadding()
                                .animateContentSize()
                                .fillMaxWidth()
                    )
                }
                RestoreStage.Birthday -> {
                    // No content. The action button is part of scrollable content.
                }
            }
        },
        content = { paddingValues ->
            val commonModifier =
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    )

            when (currentStage) {
                RestoreStage.Seed -> {
                    if (shouldSecureScreen) {
                        SecureScreen()
                    }
                    RestoreSeedMainContent(
                        userWordList = userWordList,
                        isSeedValid = isSeedValid,
                        text = text,
                        setText = { text = it },
                        parseResult = parseResult,
                        paste = paste,
                        goNext = { restoreState.goNext() },
                        modifier = commonModifier
                    )
                }
                RestoreStage.Birthday -> {
                    RestoreBirthdayMainContent(
                        zcashNetwork = zcashNetwork,
                        initialRestoreHeight = restoreHeight,
                        setRestoreHeight = setRestoreHeight,
                        onDone = onFinished,
                        modifier =
                            commonModifier
                                .imePadding()
                                .navigationBarsPadding()
                    )
                }
            }
        }
    )
}

@Composable
private fun ClearSeedMenuItem(
    modifier: Modifier = Modifier,
    onSeedClear: () -> Unit,
) {
    Reference(
        text = stringResource(id = R.string.restore_button_clear),
        onClick = onSeedClear,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(all = ZcashTheme.dimens.spacingDefault)
    )
}

@Composable
private fun RestoreSeedTopAppBar(
    onBack: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GridBgSmallTopAppBar(
        modifier = modifier,
        backText = stringResource(id = R.string.restore_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.restore_back_content_description),
        onBack = onBack,
        regularActions = {
            ClearSeedMenuItem(
                onSeedClear = onClear
            )
        },
    )
}

@Composable
private fun RestoreSeedBirthdayTopAppBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GridBgSmallTopAppBar(
        modifier = modifier,
        backText = stringResource(id = R.string.restore_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.restore_back_content_description),
        onBack = onBack,
    )
}

@Suppress("UNUSED_PARAMETER", "LongParameterList", "LongMethod")
@Composable
private fun RestoreSeedMainContent(
    userWordList: WordList,
    isSeedValid: Boolean,
    text: String,
    setText: (String) -> Unit,
    parseResult: ParseResult,
    paste: () -> String?,
    goNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val textFieldScrollToHeight = rememberSaveable { mutableIntStateOf(0) }

    if (parseResult is ParseResult.Add) {
        setText("")
        userWordList.append(parseResult.words)
    }

    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Used to calculate necessary scroll to have the seed TextFiled visible
        Column(
            modifier =
                Modifier.onSizeChanged { size ->
                    textFieldScrollToHeight.intValue = size.height
                    Twig.debug { "TextField scroll height: ${textFieldScrollToHeight.intValue}" }
                }
        ) {
            TopScreenLogoTitle(
                title = stringResource(R.string.restore_title),
                logoContentDescription = stringResource(R.string.zcash_logo_content_description),
            )

            Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

            Body(
                text = stringResource(id = R.string.restore_seed_instructions),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        SeedGridWithText(
            text = text,
            userWordList = userWordList,
            focusRequester = focusRequester,
            parseResult = parseResult,
            setText = setText
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onClick = goNext,
            enabled = isSeedValid,
            text = stringResource(id = R.string.restore_seed_button_next),
            outerPaddingValues = PaddingValues(top = ZcashTheme.dimens.spacingSmall),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }

    if (isSeedValid) {
        // Clear focus and hide keyboard to make it easier for users to see the next button
        LocalSoftwareKeyboardController.current?.hide()
        LocalFocusManager.current.clearFocus()
    }

    DisposableEffect(parseResult) {
        // Causes the TextFiled to refocus
        if (!isSeedValid) {
            focusRequester.requestFocus()
        }
        // Causes scroll to the TextField after the first type action
        if (text.isNotEmpty() && userWordList.current.value.isEmpty()) {
            scope.launch {
                scrollState.animateScrollTo(textFieldScrollToHeight.intValue)
            }
        }
        onDispose { /* Nothing to dispose */ }
    }
}

@Composable
private fun RestoreSeedBottomBar(
    userWordList: WordList,
    isSeedValid: Boolean,
    parseResult: ParseResult,
    setText: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Hide the field once the user has completed the seed phrase; if they need the field back then
    // the user can hit the clear button
    if (!isSeedValid) {
        Column(
            modifier = modifier
        ) {
            Warn(
                parseResult = parseResult,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = ZcashTheme.dimens.spacingDefault,
                            vertical = ZcashTheme.dimens.spacingSmall
                        )
            )
            Autocomplete(parseResult = parseResult, {
                setText("")
                userWordList.append(listOf(it))
            })
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
private fun SeedGridWithText(
    text: String,
    setText: (String) -> Unit,
    userWordList: WordList,
    focusRequester: FocusRequester,
    parseResult: ParseResult,
    modifier: Modifier = Modifier
) {
    val currentUserWordList = userWordList.current.collectAsStateWithLifecycle().value

    val currentSeedText =
        currentUserWordList.run {
            if (isEmpty()) {
                text
            } else {
                joinToString(separator = " ", postfix = " ").plus(text)
            }
        }

    Column(
        modifier =
            Modifier
                .border(
                    border =
                        BorderStroke(
                            width = ZcashTheme.dimens.layoutStroke,
                            color = ZcashTheme.colors.layoutStroke
                        )
                )
                .fillMaxWidth()
                .defaultMinSize(minHeight = ZcashTheme.dimens.textFieldSeedPanelDefaultHeight)
                .then(modifier)
                .testTag(RestoreTag.CHIP_LAYOUT)
    ) {
        /*
         * Treat the user input as a password for more secure input, but disable the transformation
         * to obscure typing.
         */
        TextField(
            textStyle = ZcashTheme.extendedTypography.textFieldValue,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(ZcashTheme.dimens.spacingTiny)
                    .testTag(RestoreTag.SEED_WORD_TEXT_FIELD)
                    .focusRequester(focusRequester),
            value =
                TextFieldValue(
                    text = currentSeedText,
                    selection = TextRange(index = currentSeedText.length)
                ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.restore_seed_hint),
                    style = ZcashTheme.extendedTypography.textFieldHint,
                    color = ZcashTheme.colors.textFieldHint
                )
            },
            onValueChange = {
                processTextInput(
                    currentSeedText = currentSeedText,
                    updateSeedText = it.text,
                    userWordList = userWordList,
                    setText = setText
                )
            },
            keyboardOptions =
                KeyboardOptions(
                    KeyboardCapitalization.None,
                    autoCorrect = false,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
            keyboardActions = KeyboardActions(onAny = {}),
            isError = parseResult is ParseResult.Warn,
            colors =
                TextFieldDefaults.colors(
                    cursorColor = ZcashTheme.colors.textPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
        )
    }
}

val pasteSeedWordRegex by lazy { Regex("\\s\\S") } // $NON-NLS
val whiteSpaceRegex by lazy { "\\s".toRegex() } // $NON-NLS

// TODO [#1061]: Restore screen input validation refactoring and adding tests
// TODO [#1061]: https://github.com/Electric-Coin-Company/zashi-android/issues/1061

/**
 * This function processes the text from user input after every change. It compares with what is already typed in. It
 * does a simple validation as well.
 *
 * @param currentSeedText Previously typed in text
 * @param updateSeedText Updated text after every user input
 * @param userWordList Validated type-safe list of seed words
 * @param setText New text callback
 */
fun processTextInput(
    currentSeedText: String,
    updateSeedText: String,
    userWordList: WordList,
    setText: (String) -> Unit
) {
    val textDifference =
        if (updateSeedText.length > currentSeedText.length) {
            updateSeedText.substring(currentSeedText.length)
        } else {
            ""
        }
    Twig.debug { "Text difference: $textDifference" }

    if (whiteSpaceRegex.matches(textDifference)) {
        // User tried to type a white space without confirming a valid seed word
    } else if (pasteSeedWordRegex.containsMatchIn(textDifference)) {
        // User pasted their seed from the device buffer
        setText(updateSeedText)
    } else if (updateSeedText < currentSeedText &&
        whiteSpaceRegex.matches(currentSeedText.last().toString()) &&
        currentSeedText.isNotEmpty()
    ) {
        // User backspaced to a previously confirmed word - remove it
        userWordList.removeLast()
    } else {
        // User typed in a character
        setText(updateSeedText.split(whiteSpaceRegex).last())
    }
}

@Composable
@Suppress("UNUSED_VARIABLE")
private fun Autocomplete(
    parseResult: ParseResult,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO [#1061]: Restore screen input validation refactoring and adding tests
    // TODO [#1061]: https://github.com/Electric-Coin-Company/zashi-android/issues/1061
    // Note that we currently do not use the highlighting of the suggestion bar
    val (isHighlight, suggestions) =
        when (parseResult) {
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
        LazyRow(
            modifier =
                modifier
                    .testTag(RestoreTag.AUTOCOMPLETE_LAYOUT)
                    .fillMaxWidth(),
            contentPadding = PaddingValues(all = ZcashTheme.dimens.spacingSmall),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            items(it) {
                ChipOnSurface(
                    text = it,
                    onClick = { onSuggestionSelected(it) },
                    modifier = Modifier.testTag(RestoreTag.AUTOCOMPLETE_ITEM)
                )
            }
        }
    }
}

@Composable
private fun Warn(
    parseResult: ParseResult,
    modifier: Modifier = Modifier
) {
    if (parseResult is ParseResult.Warn) {
        Surface(
            shape = RoundedCornerShape(size = ZcashTheme.dimens.tinyRippleEffectCorner),
            modifier =
                modifier.then(
                    Modifier.border(
                        border =
                            BorderStroke(
                                width = ZcashTheme.dimens.chipStroke,
                                color = ZcashTheme.colors.layoutStrokeSecondary
                            ),
                        shape = RoundedCornerShape(size = ZcashTheme.dimens.tinyRippleEffectCorner),
                    )
                ),
            color = ZcashTheme.colors.primaryColor,
            shadowElevation = ZcashTheme.dimens.chipShadowElevation
        ) {
            Text(
                color = ZcashTheme.colors.textPrimary,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(ZcashTheme.dimens.spacingSmall),
                textAlign = TextAlign.Center,
                text =
                    if (parseResult.suggestions.isEmpty()) {
                        stringResource(id = R.string.restore_seed_warning_no_suggestions)
                    } else {
                        stringResource(id = R.string.restore_seed_warning_suggestions)
                    }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("LongMethod")
private fun RestoreBirthdayMainContent(
    zcashNetwork: ZcashNetwork,
    initialRestoreHeight: BlockHeight?,
    setRestoreHeight: (BlockHeight?) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    val (height, setHeight) =
        rememberSaveable {
            mutableStateOf(initialRestoreHeight?.value?.toString() ?: "")
        }

    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopScreenLogoTitle(
            title = stringResource(R.string.restore_birthday_header),
            logoContentDescription = stringResource(R.string.zcash_logo_content_description),
        )

        Body(stringResource(R.string.restore_birthday_sub_header))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        FormTextField(
            value = height,
            onValueChange = { heightString ->
                val filteredHeightString = heightString.filter { it.isDigit() }
                setHeight(filteredHeightString)
            },
            colors =
                TextFieldDefaults.colors(
                    cursorColor = ZcashTheme.colors.textPrimary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = ZcashTheme.colors.secondaryDividerColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            textStyle = ZcashTheme.extendedTypography.textFieldBirthday,
            keyboardOptions =
                KeyboardOptions(
                    KeyboardCapitalization.None,
                    autoCorrect = false,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
            keyboardActions = KeyboardActions(onAny = {}),
            withBorder = false,
            testTag = RestoreTag.BIRTHDAY_TEXT_FIELD
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        // Empty birthday value is a valid birthday height too, thus run validation only in case of non-empty heights.
        val isBirthdayValid =
            height.isEmpty() || height.toLongOrNull()?.let {
                it >= zcashNetwork.saplingActivationHeight.value
            } ?: false

        val isEmptyBirthday = height.isEmpty()

        PrimaryButton(
            onClick = {
                if (isEmptyBirthday) {
                    setRestoreHeight(null)
                } else if (isBirthdayValid) {
                    setRestoreHeight(BlockHeight.new(zcashNetwork, height.toLong()))
                } else {
                    error("The restore button should not expect click events")
                }
                onDone()
            },
            text = stringResource(R.string.restore_birthday_button_restore),
            enabled = isBirthdayValid,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }

    LaunchedEffect(Unit) {
        // Causes the TextFiled to focus on the first screen visit
        focusRequester.requestFocus()
    }
}
