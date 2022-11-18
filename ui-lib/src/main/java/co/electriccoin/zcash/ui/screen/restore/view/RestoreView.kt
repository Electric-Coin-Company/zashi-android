package co.electriccoin.zcash.ui.screen.restore.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.model.ParseResult
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import co.electriccoin.zcash.ui.screen.restore.state.wordValidation
import kotlinx.coroutines.launch

@Preview("Restore Wallet")
@Composable
fun PreviewRestore() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            RestoreWallet(
                completeWordList = setOf(
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RestoreWallet(
    completeWordList: Set<String>,
    userWordList: WordList,
    onBack: () -> Unit,
    paste: () -> String?,
    onFinished: () -> Unit
) {
    var textState by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val parseResult = ParseResult.new(completeWordList, textState)

    SecureScreen()
    userWordList.wordValidation().collectAsState(null).value?.let { seedPhraseValidation ->
        if (seedPhraseValidation !is SeedPhraseValidation.Valid) {
            Scaffold(topBar = {
                RestoreTopAppBar(onBack = onBack, onClear = { userWordList.set(emptyList()) })
            }, bottomBar = {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Warn(parseResult)
                        Autocomplete(parseResult = parseResult) {
                            textState = ""
                            userWordList.append(listOf(it))
                            focusRequester.requestFocus()
                        }
                        NextWordTextField(
                            modifier = Modifier.focusRequester(focusRequester),
                            parseResult = parseResult,
                            text = textState,
                            setText = { textState = it }
                        )
                    }
                }) { paddingValues ->
                RestoreMainContent(
                    paddingValues = paddingValues,
                    userWordList = userWordList,
                    onTextStateChange = { textState = it },
                    focusRequester = focusRequester,
                    parseResult = parseResult,
                    paste = paste
                )
            }
        } else {
            // In some cases we need to hide the software keyboard manually, as it stays shown after
            // all words are filled successfully.
            LocalSoftwareKeyboardController.current?.hide()

            RestoreComplete(onComplete = onFinished)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RestoreTopAppBar(onBack: () -> Unit, onClear: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.restore_header)) },
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
            NavigationButton(onClick = onClear, stringResource(R.string.restore_button_clear))
        }
    )
}

// TODO [#672] Implement custom seed phrase pasting for wallet import
// TODO [#672] https://github.com/zcash/secant-android-wallet/issues/672
@Suppress("UNUSED_PARAMETER", "LongParameterList")
@Composable
private fun RestoreMainContent(
    paddingValues: PaddingValues,
    userWordList: WordList,
    onTextStateChange: (String) -> Unit,
    focusRequester: FocusRequester,
    parseResult: ParseResult,
    paste: () -> String?
) {
    val currentUserWordList = userWordList.current.collectAsState().value
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    if (parseResult is ParseResult.Add) {
        onTextStateChange("")
        userWordList.append(parseResult.words)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(scrollState)
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        Header(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.restore_title)
        )
        Body(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.restore_instructions)
        )

        ChipGridWithText(currentUserWordList)
    }

// Cause text field to refocus
    DisposableEffect(parseResult) {
        focusRequester.requestFocus()
        scope.launch {
            scrollState.scrollTo(scrollState.maxValue)
        }
        onDispose { }
    }
}

@Composable
private fun ChipGridWithText(
    userWordList: List<String>
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
    modifier: Modifier = Modifier,
    parseResult: ParseResult,
    text: String,
    setText: (String) -> Unit
) {
    /*
     * Treat the user input as a password, but disable the transformation to obscure input.
     */
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 8.dp
    ) {
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
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
    modifier: Modifier = Modifier,
    parseResult: ParseResult,
    onSuggestionSelected: (String) -> Unit
) {
    val (isHighlight, suggestions) = when (parseResult) {
        is ParseResult.Autocomplete -> {
            Pair(false, parseResult.suggestions)
        }

        is ParseResult.Warn -> {
            Pair(true, parseResult.suggestions)
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
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondary,
            shadowElevation = 4.dp
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                text = if (parseResult.suggestions.isEmpty()) {
                    stringResource(id = R.string.restore_warning_no_suggestions)
                } else {
                    stringResource(id = R.string.restore_warning_suggestions)
                }
            )
        }
    }
}

@Composable
private fun RestoreComplete(onComplete: () -> Unit) {
    Column {
        Header(stringResource(R.string.restore_complete_header))
        Body(stringResource(R.string.restore_complete_info))
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )
        PrimaryButton(onComplete, stringResource(R.string.restore_button_see_wallet))
        // TODO [#151]: Add option to provide wallet birthday
    }
}
