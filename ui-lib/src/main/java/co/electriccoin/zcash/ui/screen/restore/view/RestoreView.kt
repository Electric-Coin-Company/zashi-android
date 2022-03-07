package co.electriccoin.zcash.ui.screen.restore.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.sdk.model.SeedPhraseValidation
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.CHIP_GRID_ROW_SIZE
import co.electriccoin.zcash.ui.design.component.Chip
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.NavigationButton
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TextField
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restore.RestoreTag
import co.electriccoin.zcash.ui.screen.restore.model.ParseResult
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import co.electriccoin.zcash.ui.screen.restore.state.wordValidation

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
                    "abstract"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestoreWallet(
    completeWordList: Set<String>,
    userWordList: WordList,
    onBack: () -> Unit,
    paste: () -> String?,
    onFinished: () -> Unit
) {
    userWordList.wordValidation().collectAsState(null).value?.let { seedPhraseValidation ->
        if (seedPhraseValidation !is SeedPhraseValidation.Valid) {
            Scaffold(topBar = {
                RestoreTopAppBar(onBack = onBack, onClear = { userWordList.set(emptyList()) })
            }) {
                RestoreMainContent(completeWordList, userWordList, paste)
            }
        } else {
            RestoreComplete(onComplete = onFinished)
        }
    }
}

@Composable
private fun RestoreTopAppBar(onBack: () -> Unit, onClear: () -> Unit) {
    SmallTopAppBar(
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

@Suppress("UNUSED_PARAMETER")
@Composable
private fun RestoreMainContent(
    completeWordList: Set<String>,
    userWordList: WordList,
    paste: () -> String?
) {
    var textState by rememberSaveable { mutableStateOf("") }

    val currentUserWordList = userWordList.current.collectAsState().value

    val parseResult = ParseResult.new(completeWordList, textState)

    if (parseResult is ParseResult.Add) {
        textState = ""
        userWordList.append(parseResult.words)
    }

    val focusRequester = remember { FocusRequester() }

    Column {
        Text(text = stringResource(id = R.string.restore_instructions))

        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .weight(MINIMAL_WEIGHT)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                ChipGridWithText(currentUserWordList, textState, { textState = it }, focusRequester)
                Spacer(
                    Modifier
                        .fillMaxHeight()
                        .weight(MINIMAL_WEIGHT)
                )
            }

            // Must come after the grid in order for its Z ordering to be on top
            Warn(parseResult)

            Autocomplete(Modifier.align(Alignment.BottomStart), parseResult) {
                textState = ""
                userWordList.append(listOf(it))
                focusRequester.requestFocus()
            }
        }
    }

    // Cause text field to refocus
    DisposableEffect(parseResult) {
        focusRequester.requestFocus()
        onDispose { }
    }
}

@Composable
private fun ChipGridWithText(
    userWordList: List<String>,
    text: String,
    setText: (String) -> Unit,
    focusRequester: FocusRequester
) {
    val isTextFieldOnNewLine = userWordList.size % CHIP_GRID_ROW_SIZE == 0

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .verticalScroll(scrollState)
            .testTag(CommonTag.CHIP_LAYOUT)
    ) {
        userWordList.chunked(CHIP_GRID_ROW_SIZE).forEachIndexed { chunkIndex, chunk ->
            Row(Modifier.fillMaxWidth()) {
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
                    NextWordTextField(
                        Modifier
                            .focusRequester(focusRequester)
                            .weight((CHIP_GRID_ROW_SIZE - chunk.size) * singleItemWeight),
                        text, setText
                    )
                }
            }
        }

        if (isTextFieldOnNewLine) {
            NextWordTextField(Modifier.focusRequester(focusRequester), text = text, setText = setText)
        }
    }
}

@Composable
private fun NextWordTextField(modifier: Modifier = Modifier, text: String, setText: (String) -> Unit) {
    /*
     * Treat the user input as a password, but disable the transformation to obscure input.
     */
    TextField(
        value = text, onValueChange = setText,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .testTag(RestoreTag.SEED_WORD_TEXT_FIELD),
        visualTransformation = VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            KeyboardCapitalization.None,
            autoCorrect = false, imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onAny = {})
    )
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
                Button(
                    modifier = Modifier.testTag(RestoreTag.AUTOCOMPLETE_ITEM),
                    onClick = { onSuggestionSelected(it) }
                ) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
private fun Warn(parseResult: ParseResult) {
    if (parseResult is ParseResult.Warn) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                Modifier
                    .matchParentSize()
                    .background(ZcashTheme.colors.overlay)
            )

            if (parseResult.suggestions.isEmpty()) {
                Text(stringResource(id = R.string.restore_warning_no_suggestions))
            } else {
                Text(stringResource(id = R.string.restore_warning_suggestions))
            }
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
