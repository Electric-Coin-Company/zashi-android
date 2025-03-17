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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    handle: SeedTextFieldHandle = rememberSeedTextFieldHandle(),
) {
    val interactions = remember { state.values.map { MutableInteractionSource() } }
    val focusRequesters = remember { state.values.map { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(handle.selectedIndex) {
        if (handle.selectedIndex >= 0) {
            focusRequesters[handle.selectedIndex].requestFocus()
        } else {
            focusManager.clearFocus(true)
        }
    }

    LaunchedEffect(handle.selectedIndex, state.values) {
        if (handle.selectedIndex >= 0) {
            handle.selectedText = state.values[handle.selectedIndex].value
        } else {
            handle.selectedText = null
        }
    }

    LaunchedEffect(interactions) {
        interactions
            .observeSelectedIndex()
            .collect { index ->
                handle.selectedIndex = index
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
            val focusRequester = remember { focusRequesters[index] }
            val interaction = remember { interactions[index] }
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
                                    handle.requestPreviousFocus()
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        },
                prefix = (index + 1).toString(),
                state =
                    wordState.copy(
                        onValueChange = {
                            wordState.onValueChange(it)
                            if (index == handle.selectedIndex) {
                                handle.selectedText = it
                            }
                        }
                    ),
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
                        keyboardType = KeyboardType.Text,
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

@Stable
class SeedTextFieldHandle {
    var selectedText: String? by mutableStateOf(null)
    var selectedIndex by mutableIntStateOf(-1)

    @Suppress("MagicNumber")
    fun requestNextFocus() {
        if (selectedIndex == 23) {
            selectedIndex = -1
        } else {
            selectedIndex += 1
        }
    }

    fun requestPreviousFocus() {
        if (selectedIndex >= 0) {
            selectedIndex -= 1
        } else {
            selectedIndex = -1
        }
    }
}

@Composable
fun rememberSeedTextFieldHandle(): SeedTextFieldHandle = remember { SeedTextFieldHandle() }

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
