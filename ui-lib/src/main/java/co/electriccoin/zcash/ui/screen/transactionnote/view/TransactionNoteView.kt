package co.electriccoin.zcash.ui.screen.transactionnote.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.getColor
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionnote.model.TransactionNoteState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TransactionNoteView(
    state: TransactionNoteState,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        state = state,
        sheetState = sheetState,
        content = {
            BottomSheetContent(state)
        },
    )
}

@Composable
private fun BottomSheetContent(state: TransactionNoteState) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = state.title.getValue(),
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )

        Spacer(Modifier.height(28.dp))

        ZashiTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            state = state.note,
            minLines = 4,
            placeholder = {
                Text(
                    text = "Write an optional note to describe this transaction...",
                    style = ZashiTypography.textMd,
                    color = ZashiColors.Inputs.Default.text
                )
            }
        )

        Spacer(Modifier.height(6.dp))
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = state.noteCharacters.getValue(),
            style = ZashiTypography.textSm,
            color = state.noteCharacters.getColor()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.negative?.let {
                ZashiButton(
                    state = it,
                    modifier = Modifier.weight(1f),
                    colors = ZashiButtonDefaults.destructive1Colors()
                )
            }

            state.secondaryButton?.let {
                ZashiButton(
                    state = it,
                    modifier = Modifier.weight(1f),
                    colors = ZashiButtonDefaults.tertiaryColors()
                )
            }

            state.primaryButton?.let {
                ZashiButton(
                    state = it,
                    modifier = Modifier.weight(1f),
                    colors = ZashiButtonDefaults.primaryColors()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        TransactionNoteView(
            state =
                TransactionNoteState(
                    onBack = {},
                    title = stringRes("Title"),
                    note = TextFieldState(stringRes("")) {},
                    noteCharacters =
                        StyledStringResource(
                            stringRes("x/y characters")
                        ),
                    primaryButton = null,
                    secondaryButton = null,
                    negative = ButtonState(stringRes("Delete note")),
                ),
            sheetState =
                rememberModalBottomSheetState(
                    skipHiddenState = true,
                    skipPartiallyExpanded = true,
                    initialValue = SheetValue.Expanded,
                    confirmValueChange = { true }
                )
        )
    }
