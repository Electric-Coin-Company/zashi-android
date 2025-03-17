@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.restore.seed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.SeedTextFieldHandle
import co.electriccoin.zcash.ui.design.component.SeedTextFieldState
import co.electriccoin.zcash.ui.design.component.SeedWordTextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiChipButton
import co.electriccoin.zcash.ui.design.component.ZashiChipButtonState
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSeedTextField
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.rememberSeedTextFieldHandle
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RestoreSeedView(
    state: RestoreSeedState,
    suggestionsState: RestoreSeedSuggestionsState
) {
    val handle = rememberSeedTextFieldHandle()

    BlankBgScaffold(
        topBar = { AppBar(state) },
        bottomBar = { BottomBar(state, suggestionsState, handle) },
        content = { padding ->
            Content(
                state = state,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .scaffoldPadding(padding),
                handle = handle
            )
        }
    )
}

@Composable
private fun Content(
    state: RestoreSeedState,
    handle: SeedTextFieldHandle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.restore_subtitle),
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.restore_message),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(20.dp))
        ZashiSeedTextField(
            state = state.seed,
            handle = handle
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(24.dp))
        ZashiButton(
            state.nextButton,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AppBar(state: RestoreSeedState) {
    ZashiSmallTopAppBar(
        title = stringResource(R.string.restore_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(
                onBack = state.onBack,
                modifier = Modifier.testTag(ZashiTopAppBarTags.BACK)
            )
        },
        regularActions = {
            ZashiIconButton(state.dialogButton, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(20.dp))
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
    )
}

@Composable
private fun BottomBar(
    state: RestoreSeedState,
    suggestionsState: RestoreSeedSuggestionsState,
    handle: SeedTextFieldHandle,
    modifier: Modifier = Modifier,
) {
    if (suggestionsState.isVisible && handle.selectedIndex >= 0) {
        Column(
            modifier = modifier
        ) {
            val suggestions by getFilteredSuggestions(suggestionsState, handle)

            if (suggestions.isEmpty()) {
                Warn()
            } else {
                LaunchedEffect(suggestions) {
                    handle.observeSelectedTextChanged().collect {
                        if (suggestions.contains(handle.selectedText)) {
                            handle.requestNextFocus()
                        }
                    }
                }

                LazyRow(
                    modifier =
                        Modifier
                            .testTag(RestoreSeedTag.AUTOCOMPLETE_LAYOUT)
                            .fillMaxWidth(),
                    contentPadding = PaddingValues(ZcashTheme.dimens.spacingSmall),
                    horizontalArrangement = spacedBy(6.dp)
                ) {
                    items(suggestions) {
                        ZashiChipButton(
                            state =
                                ZashiChipButtonState(
                                    text = stringRes(it),
                                    onClick = {
                                        if (handle.selectedIndex >= 0) {
                                            state.seed.values[handle.selectedIndex].onValueChange(it)
                                            handle.requestNextFocus()
                                        }
                                    }
                                ),
                            modifier = Modifier.testTag(RestoreSeedTag.AUTOCOMPLETE_ITEM)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Warn(modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.restore_seed_warning_suggestions)
        )
    }
}

@Composable
private fun getFilteredSuggestions(
    suggestionsState: RestoreSeedSuggestionsState,
    handle: SeedTextFieldHandle,
): State<List<String>> = produceState(
    initialValue = suggestionsState.suggestions,
    key1 = suggestionsState.suggestions,
    key2 = handle.selectedText,
) {
    withContext(Dispatchers.Default) {
        delay(150.milliseconds)
        val trimmed = handle.selectedText?.lowercase(Locale.US)?.trim().orEmpty()
        val autocomplete = suggestionsState.suggestions.filter { it.startsWith(trimmed) }
        value = when {
            trimmed.isBlank() -> suggestionsState.suggestions
            suggestionsState.suggestions.contains(trimmed) && autocomplete.size == 1 -> suggestionsState.suggestions
            else -> autocomplete
        }
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        RestoreSeedView(
            state =
                RestoreSeedState(
                    seed =
                        SeedTextFieldState(
                            values =
                                (1..24).map {
                                    SeedWordTextFieldState(
                                        value = "Word",
                                        onValueChange = { },
                                        isError = false
                                    )
                                }
                        ),
                    onBack = {},
                    dialogButton = IconButtonState(R.drawable.ic_restore_dialog) {},
                    nextButton =
                        ButtonState(
                            text = stringRes("Next"),
                            onClick = {}
                        )
                ),
            suggestionsState =
                RestoreSeedSuggestionsState(
                    isVisible = true,
                    suggestions = listOf("Word 1", "Word 2"),
                )
        )
    }
