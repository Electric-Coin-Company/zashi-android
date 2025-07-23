package co.electriccoin.zcash.ui.screen.receive.info

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiBulletText
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun TransparentAddressInfoScreen() {
    val navigationRouter = koinInject<NavigationRouter>()
    BackHandler { navigationRouter.back() }
    View { navigationRouter.back() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun View(onDismissRequest: () -> Unit) {
    ZashiScreenModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_receive_info_transparent),
                contentDescription = null
            )
            Spacer(12.dp)
            Text(
                text = stringResource(R.string.receive_info_transparent_title),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(12.dp)
            ZashiBulletText(
                stringResource(R.string.receive_info_transparent_bullet_1),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
            )
            Spacer(8.dp)
            ZashiBulletText(
                stringResource(R.string.receive_info_transparent_bullet_2),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
            )
            Spacer(8.dp)
            ZashiBulletText(
                stringResource(R.string.receive_info_transparent_bullet_3),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
            )
            Spacer(8.dp)
            ZashiBulletText(
                stringResource(R.string.receive_info_transparent_bullet_4),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
            )
            Spacer(32.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.general_ok),
                onClick = onDismissRequest
            )
        }
    }
}

@Serializable
data object TransparentAddressInfoArgs

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        View { }
    }
