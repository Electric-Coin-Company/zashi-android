package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreenSizes
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZashiSeedTextField(
    state: SeedTextFieldState,
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember { state.values.map { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = 3,
        horizontalArrangement = spacedBy(4.dp),
        verticalArrangement = spacedBy(4.dp),
        overflow = FlowRowOverflow.Visible,
    ) {
        state.values.forEachIndexed { index, wordState ->
            val focusRequester = remember { focusRequesters[index] }
            ZashiSeedWordTextField(
                modifier =
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                prefix = (index + 1).toString(),
                state = wordState,
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus(true)
                        },
                        onNext = {
                            if (index != state.values.lastIndex) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = if (index == state.values.lastIndex) ImeAction.Done else ImeAction.Next
                    ),
            )
        }
    }
}

@Immutable
data class SeedTextFieldState(
    val values: List<SeedWordTextFieldState>,
)

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
                                    value = stringRes("Word"),
                                    onValueChange = { },
                                    isError = false
                                )
                            }
                    )
            )
        }
    }
