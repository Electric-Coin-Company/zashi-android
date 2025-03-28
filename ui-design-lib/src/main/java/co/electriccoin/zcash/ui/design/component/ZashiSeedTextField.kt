package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreenSizes
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.combineToFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZashiSeedTextField(
    state: SeedTextFieldState,
    modifier: Modifier = Modifier,
    wordModifier: (index: Int) -> Modifier = { Modifier },
    handle: SeedTextFieldHandle = rememberSeedTextFieldHandle(),
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.values.map { it.value }) {
        val newValues = state.values.map { it.value }
        handle.internalState =
            handle.internalState.copy(
                texts = newValues,
                selectedText =
                    if (handle.internalState.selectedIndex <= -1) {
                        null
                    } else {
                        newValues[handle.internalState.selectedIndex]
                    }
            )
    }

    LaunchedEffect(handle.selectedIndex) {
        if (handle.selectedIndex >= 0) {
            handle.focusRequesters[handle.selectedIndex].requestFocus()
        } else {
            focusManager.clearFocus(true)
        }
    }

    LaunchedEffect(Unit) {
        handle.interactions
            .observeSelectedIndex()
            .collect { index ->
                handle.setSelectedIndex(index)
            }
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = 3,
        horizontalArrangement = spacedBy(4.dp),
        verticalArrangement = spacedBy(4.dp),
        overflow = FlowRowOverflow.Visible,
    ) {
        state.values.forEachIndexed { index, wordState ->
            val focusRequester = remember { handle.focusRequesters[index] }
            val interaction = remember { handle.interactions[index] }
            val textFieldHandle = remember { handle.textFieldHandles[index] }
            val previousHandle =
                remember {
                    if (index > 0) handle.textFieldHandles[index - 1] else null
                }
            ZashiSeedWordTextField(
                modifier =
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onKeyEvent { event ->
                            when {
                                event.key == Key.Spacebar -> {
                                    handle.requestNextFocus()
                                    true
                                }

                                event.key == Key.Backspace && wordState.value.isEmpty() -> {
                                    previousHandle?.moveCursorToEnd()
                                    handle.requestPreviousFocus()
                                    true
                                }

                                else -> {
                                    false
                                }
                            }
                        },
                handle = textFieldHandle,
                innerModifier = wordModifier(index),
                prefix = (index + 1).toString(),
                state = wordState,
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            handle.requestNextFocus()
                        },
                        onNext = {
                            handle.requestNextFocus()
                        },
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        autoCorrectEnabled = false,
                        imeAction = if (index == state.values.lastIndex) ImeAction.Done else ImeAction.Next
                    ),
                interactionSource = interaction
            )
        }
    }
}

private fun List<MutableInteractionSource>.observeSelectedIndex() =
    this
        .map { interaction ->
            interaction.isFocused()
        }
        .combineToFlow()
        .map {
            it.indexOfFirst { isFocused -> isFocused }
        }

private fun InteractionSource.isFocused(): Flow<Boolean> =
    channelFlow {
        val focusInteractions = mutableListOf<FocusInteraction.Focus>()
        val isFocused = MutableStateFlow(false)

        launch {
            interactions.collect { interaction ->
                when (interaction) {
                    is FocusInteraction.Focus -> focusInteractions.add(interaction)
                    is FocusInteraction.Unfocus -> focusInteractions.remove(interaction.focus)
                }
                isFocused.update { focusInteractions.isNotEmpty() }
            }
        }

        launch {
            isFocused.collect {
                send(it)
            }
        }

        awaitClose {
            // do nothing
        }
    }

@Immutable
data class SeedTextFieldState(
    val values: List<SeedWordTextFieldState>,
)

@Suppress("MagicNumber")
@Stable
class SeedTextFieldHandle(seedTextFieldState: SeedTextFieldState, selectedIndex: Int) {
    internal val textFieldHandles = seedTextFieldState.values.map { ZashiTextFieldHandle(it.value) }

    internal val interactions = List(24) { MutableInteractionSource() }

    internal val focusRequesters = List(24) { FocusRequester() }

    internal var internalState by mutableStateOf(
        SeedTextFieldInternalState(
            selectedIndex = selectedIndex,
            selectedText = null,
            texts = seedTextFieldState.values.map { it.value }
        )
    )

    val selectedText: String? by derivedStateOf { internalState.selectedText }

    val selectedIndex by derivedStateOf { internalState.selectedIndex }

    @Suppress("MagicNumber")
    fun requestNextFocus() {
        internalState =
            if (internalState.selectedIndex == 23) {
                internalState.copy(
                    selectedIndex = -1,
                    selectedText = null,
                )
            } else {
                internalState.copy(
                    selectedIndex = internalState.selectedIndex + 1,
                    selectedText = internalState.texts[internalState.selectedIndex + 1],
                )
            }
    }

    fun requestPreviousFocus() {
        internalState =
            if (internalState.selectedIndex >= 1) {
                internalState.copy(
                    selectedIndex = internalState.selectedIndex - 1,
                    selectedText = internalState.texts[internalState.selectedIndex - 1]
                )
            } else {
                internalState.copy(
                    selectedIndex = -1,
                    selectedText = null,
                )
            }
    }

    fun setSelectedIndex(index: Int) {
        internalState =
            internalState.copy(
                selectedIndex = index,
                selectedText = if (index <= -1) null else internalState.texts[index]
            )
    }
}

internal data class SeedTextFieldInternalState(
    val selectedIndex: Int,
    val selectedText: String?,
    val texts: List<String>
)

@Suppress("MagicNumber")
@Composable
fun rememberSeedTextFieldHandle(
    seedTextFieldState: SeedTextFieldState =
        SeedTextFieldState(
            List(24) {
                SeedWordTextFieldState(
                    value = "",
                    onValueChange = {},
                    isError = false
                )
            }
        ),
    selectedIndex: Int = -1
): SeedTextFieldHandle = remember { SeedTextFieldHandle(seedTextFieldState, selectedIndex) }

@PreviewScreenSizes
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiSeedTextField(
                state =
                    SeedTextFieldState(
                        values =
                            (1..24).map {
                                SeedWordTextFieldState(
                                    value = "Word",
                                    onValueChange = { },
                                    isError = false
                                )
                            }
                    )
            )
        }
    }
