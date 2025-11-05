package co.electriccoin.zcash.ui.screen.hotfix.ephemeral

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiAddressTextField
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberInScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EphemeralHotfixView(
    state: EphemeralHotfixState?,
    sheetState: SheetState = rememberInScreenModalBottomSheetState(),
    onSheetOpen: (FocusRequester) -> Unit = { }
) {
    val onSheetOpen by rememberUpdatedState(onSheetOpen)

    val focusRequester = remember { FocusRequester() }

    ZashiScreenModalBottomSheet(
        state = state,
        sheetState = sheetState,
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
                "Discover Funds",
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(12.dp)
            Text(
                text =
                    "If you confirm, Zashi will scan the transparent address you provide and discover its funds. " +
                        "This may take a few minutes up to a few hours.",
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary
            )
            Spacer(24.dp)
            Text(
                "Transparent Address",
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Inputs.Default.label
            )
            Spacer(6.dp)
            ZashiAddressTextField(
                modifier = Modifier.focusRequester(focusRequester),
                state = it.address,
                placeholder = {
                    Text(text = "Enter or paste...")
                }
            )
            Spacer(32.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = it.button
            )
            Spacer(24.dp)
        }

        LaunchedEffect(sheetState.currentValue) {
            if (sheetState.currentValue == SheetValue.Expanded) {
                onSheetOpen(focusRequester)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        EphemeralHotfixView(
            EphemeralHotfixState(
                onBack = {},
                button =
                    ButtonState(
                        text = stringRes("Recover Funds"),
                        onClick = {},
                    ),
                address = TextFieldState(stringRes("")) {},
            )
        )
    }
