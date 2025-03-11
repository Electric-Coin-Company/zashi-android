package co.electriccoin.zcash.ui.screen.restore

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiInScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun RestoreSeedDialog(
    state: RestoreSeedDialogState?,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ZashiInScreenModalBottomSheet(
        state = state,
        sheetState = sheetState,
        content = {
            Content(it)
        },
    )
}

@Composable
private fun Content(state: RestoreSeedDialogState) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.integrations_dialog_more_options),
            style = ZashiTypography.header6,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(12.dp))

        Info(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = ZashiColors.Text.textPrimary)) {
                        append(stringResource(id = R.string.restore_dialog_message_1_bold_part))
                    }
                    append(" ")
                    append(stringResource(R.string.restore_dialog_message_1))
                }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Info(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = ZashiColors.Text.textPrimary)) {
                        append(stringResource(id = R.string.restore_dialog_message_2_bold_part))
                    }
                    append(" ")
                    append(stringResource(R.string.restore_dialog_message_2))
                }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.restore_dialog_button),
            onClick = state.onBack
        )

        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@Composable
private fun Info(text: AnnotatedString) {
    Row {
        Image(
            painterResource(R.drawable.ic_info),
            contentDescription = ""
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Normal,
            color = ZashiColors.Text.textTertiary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        RestoreSeedDialog(
            sheetState =
                rememberModalBottomSheetState(
                    skipPartiallyExpanded = true,
                    skipHiddenState = true,
                    initialValue = SheetValue.Expanded,
                ),
            state = RestoreSeedDialogState { },
        )
    }
