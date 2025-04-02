package co.electriccoin.zcash.ui.screen.home.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.HorizontalSpacer
import co.electriccoin.zcash.ui.design.component.VerticalSpacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun WalletBackupMessage(
    state: WalletBackupMessageState,
    contentPadding: PaddingValues
) {
    HomeMessageWrapper(
        color = ZashiColors.Utility.Espresso.utilityEspresso100,
        contentPadding = contentPadding,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_warning_triangle),
            contentDescription = null
        )
        HorizontalSpacer(16.dp)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                stringResource(R.string.home_message_backup_required_title),
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Utility.Espresso.utilityEspresso900
            )
            VerticalSpacer(2.dp)
            Text(
                text = stringResource(R.string.home_message_backup_required_subtitle),
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Utility.Espresso.utilityEspresso700
            )
        }
        ZashiButton(
            modifier = Modifier.height(36.dp),
            state =
                ButtonState(
                    onClick = state.onClick,
                    text = stringRes("Start")
                )
        )
    }
}

@Immutable
data class WalletBackupMessageState(
    val onClick: () -> Unit,
) : HomeMessageState

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            WalletBackupMessage(
                state =
                    WalletBackupMessageState(
                        onClick = {}
                    ),
                contentPadding = PaddingValues()
            )
        }
    }
