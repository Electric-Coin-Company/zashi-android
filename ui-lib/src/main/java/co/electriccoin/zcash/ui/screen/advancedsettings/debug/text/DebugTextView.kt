package co.electriccoin.zcash.ui.screen.advancedsettings.debug.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugTextView(
    state: DebugTextState?,
) {
    ZashiScreenModalBottomSheet(
        state = state,
        includeBottomPadding = false
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f, false)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp)
                    .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Text(
                text = it.title.getValue(),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(12.dp)
            SelectionContainer {
                Text(
                    text = it.text.getValue(),
                    style = ZashiTypography.textMd,
                    color = ZashiColors.Text.textPrimary
                )
            }
            Spacer(24.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        DebugTextView(
            DebugTextState(
                title = stringRes("Title"),
                text = stringRes("Text"),
                onBack = {},
            )
        )
    }
