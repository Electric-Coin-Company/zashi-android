package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
fun ZashiTopAppBarBackNavigation(onBack: () -> Unit) =
    ZashiTopAppBarNavigation(
        backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
        drawableRes = R.drawable.ic_zashi_navigation_back,
        onBack = onBack
    )

@Composable
fun ZashiTopAppBarCloseNavigation(onBack: () -> Unit) =
    ZashiTopAppBarNavigation(
        backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
        drawableRes = R.drawable.ic_navigation_close,
        onBack = onBack,
        tint = ZashiColors.Text.textPrimary
    )

@Composable
fun ZashiTopAppBarHamburgerNavigation(onBack: () -> Unit) =
    ZashiTopAppBarNavigation(
        backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
        drawableRes = R.drawable.ic_navigation_hamburger,
        onBack = onBack,
        tint = ZashiColors.Text.textPrimary
    )

@Composable
private fun ZashiTopAppBarNavigation(
    backContentDescriptionText: String,
    @DrawableRes drawableRes: Int,
    onBack: () -> Unit,
    tint: Color? = null,
) {
    Row {
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = backContentDescriptionText,
                tint = tint ?: LocalContentColor.current
            )
        }
    }
}
