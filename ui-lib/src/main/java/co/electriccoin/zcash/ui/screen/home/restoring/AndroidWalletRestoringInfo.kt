package co.electriccoin.zcash.ui.screen.home.restoring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiBulletText
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiInfoText
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidWalletRestoringInfo() {
    val viewModel = koinViewModel<WalletRestoringInfoViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Content(state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: WalletRestoringInfoState,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = state.onBack
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f, false)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = it.calculateBottomPadding()
                    )
        ) {
            Text(
                stringResource(R.string.home_info_restoring_title),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(12.dp)
            Text(
                stringResource(R.string.home_info_restoring_message),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
            Spacer(12.dp)
            ZashiBulletText(
                stringResource(R.string.home_info_restoring_bullet_1),
                stringResource(R.string.home_info_restoring_bullet_2),
                color = ZashiColors.Text.textTertiary
            )
            state.info?.let {
                Spacer(32.dp)
                ZashiInfoText(it.getValue())
            }
            Spacer(24.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state =
                    ButtonState(
                        text = stringRes(co.electriccoin.zcash.ui.design.R.string.general_ok),
                        onClick = state.onBack
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
            state =
                WalletRestoringInfoState(
                    onBack = {},
                    info = stringRes(R.string.home_info_restoring_message)
                )
        )
    }

@Serializable
object WalletRestoringInfo
