package co.electriccoin.zcash.ui.screen.home.backup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiBulletText
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiCheckbox
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletBackupInfoView(
    state: WalletBackupInfoState?,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        sheetState = sheetState,
        state = state
    ) {
        Column(
            modifier = Modifier
                .weight(1f, false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)

        ) {
            Image(
                painter = painterResource(R.drawable.ic_info_backup),
                contentDescription = null
            )
            Spacer(12.dp)
            Text(
                stringResource(R.string.home_info_backup_title),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(12.dp)
            Text(
                stringResource(R.string.home_info_backup_subtitle_1),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
            Spacer(12.dp)
            Text(
                stringResource(R.string.home_info_backup_subtitle_2),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
            Spacer(12.dp)
            ZashiBulletText(
                stringResource(R.string.home_info_backup_bullet_1),
                stringResource(R.string.home_info_backup_bullet_2),
                color = ZashiColors.Text.textTertiary
            )
            Spacer(12.dp)
            Text(
                stringResource(R.string.home_info_backup_message_1),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
            Spacer(24.dp)
            Text(
                stringResource(R.string.home_info_backup_message_2),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
            Spacer(32.dp)
            it.checkboxState?.let { checkbox ->
                ZashiCheckbox(
                    state = checkbox
                )
                Spacer(12.dp)
            }
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = it.secondaryButton,
                colors = ZashiButtonDefaults.secondaryColors()
            )
            Spacer(4.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = it.primaryButton
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        WalletBackupInfoView(
            WalletBackupInfoState(
                onBack = {},
                secondaryButton = ButtonState(
                    text = stringRes(R.string.general_remind_me_later),
                    onClick = {},
                    isEnabled = false
                ),
                primaryButton = ButtonState(
                    text = stringRes(R.string.general_ok),
                    onClick = {}
                ),
                checkboxState = CheckboxState(
                    isChecked = false,
                    onClick = {},
                    text = stringRes(R.string.home_info_backup_checkbox)
                )
            )
        )
    }
