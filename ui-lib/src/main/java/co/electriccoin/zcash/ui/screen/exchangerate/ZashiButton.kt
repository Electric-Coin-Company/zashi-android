package co.electriccoin.zcash.ui.screen.exchangerate

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
internal fun ZashiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ZashiButtonDefaults.primaryButtonColors(),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = colors,
        content = content
    )
}

object ZashiButtonDefaults {
    @Composable
    fun primaryButtonColors(
        containerColor: Color = ZcashTheme.zashiColors.btnPrimaryBg,
        contentColor: Color = ZcashTheme.zashiColors.btnPrimaryFg,
        disabledContainerColor: Color = ZcashTheme.zashiColors.btnPrimaryBgDisabled,
        disabledContentColor: Color = ZcashTheme.zashiColors.btnPrimaryFgDisabled,
    ): ButtonColors =
        ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        )

    @Composable
    fun tertiaryButtonColors(
        containerColor: Color = ZcashTheme.zashiColors.btnTertiaryBg,
        contentColor: Color = ZcashTheme.zashiColors.btnTertiaryFg,
        disabledContainerColor: Color = Color.Unspecified,
        disabledContentColor: Color = Color.Unspecified,
    ): ButtonColors =
        ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        )
}
