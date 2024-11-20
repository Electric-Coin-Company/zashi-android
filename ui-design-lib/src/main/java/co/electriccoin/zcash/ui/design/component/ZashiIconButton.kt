package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue

@Composable
fun ZashiIconButton(state: IconButtonState, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier,
        onClick = state.onClick
    ) {
        Icon(
            painter = painterResource(state.icon),
            contentDescription = state.contentDescription?.getValue(),
            tint = Color.Unspecified
        )
    }
}

data class IconButtonState(
    @DrawableRes val icon: Int,
    val contentDescription: StringResource? = null,
    val onClick: () -> Unit,
)