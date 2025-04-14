package co.electriccoin.zcash.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidWalletSyncingInfo() {
    val navigationRouter = koinInject<NavigationRouter>()
    Content(
        onBack = { navigationRouter.back() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    onBack: () -> Unit,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onBack
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
        ) {
            Text(
                stringResource(R.string.home_info_syncing_title),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(12.dp)
            Text(
                stringResource(R.string.home_info_syncing_message),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
            Spacer(32.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state =
                    ButtonState(
                        text = stringRes(R.string.general_ok),
                        onClick = onBack
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        Content(
            onBack = {},
        )
    }

@Serializable
object WalletSyncingInfo
