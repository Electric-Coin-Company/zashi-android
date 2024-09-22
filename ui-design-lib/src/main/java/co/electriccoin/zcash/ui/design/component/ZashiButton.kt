package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue

@Composable
fun ZashiButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    colors: ZashiButtonColors = ZashiButtonDefaults.primaryColors(),
    content: @Composable RowScope.(ZashiButtonScope) -> Unit = ZashiButtonDefaults.content
) {
    ZashiButton(
        text = state.text.getValue(),
        onClick = state.onClick,
        modifier = modifier,
        enabled = state.isEnabled,
        isLoading = state.isLoading,
        colors = colors,
        content = content
    )
}

@Suppress("LongParameterList")
@Composable
fun ZashiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    colors: ZashiButtonColors = ZashiButtonDefaults.primaryColors(),
    content: @Composable RowScope.(ZashiButtonScope) -> Unit = ZashiButtonDefaults.content
) {
    val scope =
        object : ZashiButtonScope {
            @Composable
            override fun Text() {
                Text(
                    text = text,
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.SemiBold
                )
            }

            @Composable
            override fun Loading() {
                if (enabled && isLoading) {
                    LottieProgress(
                        loadingRes =
                            if (isSystemInDarkTheme()) {
                                R.raw.lottie_loading
                            } else {
                                R.raw.lottie_loading_white
                            }
                    )
                }
            }
        }

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = colors.toButtonColors(),
        border = colors.borderColor.takeIf { it != Color.Unspecified }?.let { BorderStroke(1.dp, it) },
        content = {
            content(scope)
        }
    )
}

interface ZashiButtonScope {
    @Composable
    fun Text()

    @Composable
    fun Loading()
}

object ZashiButtonDefaults {
    val content: @Composable RowScope.(ZashiButtonScope) -> Unit
        get() = { scope ->
            scope.Text()
            Spacer(modifier = Modifier.width(6.dp))
            scope.Loading()
        }

    @Composable
    fun primaryColors(
        containerColor: Color = ZashiColors.Btns.Primary.btnPrimaryBg,
        contentColor: Color = ZashiColors.Btns.Primary.btnPrimaryFg,
        disabledContainerColor: Color = ZashiColors.Btns.Primary.btnPrimaryBgDisabled,
        disabledContentColor: Color = ZashiColors.Btns.Primary.btnBoldFgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        borderColor = Color.Unspecified
    )

    @Composable
    fun tertiaryColors(
        containerColor: Color = ZashiColors.Btns.Tertiary.btnTertiaryBg,
        contentColor: Color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
        disabledContainerColor: Color = ZashiColors.Btns.Tertiary.btnTertiaryBgDisabled,
        disabledContentColor: Color = ZashiColors.Btns.Tertiary.btnTertiaryFgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        borderColor = Color.Unspecified
    )

    @Composable
    fun destructive1Colors(
        containerColor: Color = ZashiColors.Btns.Destructive1.btnDestroy1Bg,
        contentColor: Color = ZashiColors.Btns.Destructive1.btnDestroy1Fg,
        borderColor: Color = ZashiColors.Btns.Destructive1.btnDestroy1Border,
        disabledContainerColor: Color = ZashiColors.Btns.Destructive1.btnDestroy1BgDisabled,
        disabledContentColor: Color = ZashiColors.Btns.Destructive1.btnDestroy1FgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        borderColor = borderColor
    )
}

@Immutable
data class ZashiButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val borderColor: Color,
)

@Composable
private fun ZashiButtonColors.toButtonColors() =
    ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun PrimaryPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Primary",
                onClick = {},
            )
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun TertiaryPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Primary",
                colors = ZashiButtonDefaults.tertiaryColors(),
                onClick = {},
            )
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun DestroyPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Primary",
                colors = ZashiButtonDefaults.destructive1Colors(),
                onClick = {},
            )
        }
    }