package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColorsInternal
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue

@Composable
fun ZashiButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    style: TextStyle = ZashiButtonDefaults.style,
    shape: Shape = ZashiButtonDefaults.shape,
    contentPadding: PaddingValues = ZashiButtonDefaults.contentPadding,
    colors: ZashiButtonColors = LocalZashiButtonColors.current ?: ZashiButtonDefaults.primaryColors(),
    content: @Composable RowScope.(ZashiButtonScope) -> Unit = ZashiButtonDefaults.content
) {
    ZashiButton(
        text = state.text.getValue(),
        icon = state.icon,
        trailingIcon = state.trailingIcon,
        onClick = state.onClick,
        modifier = modifier,
        style = style,
        shape = shape,
        contentPadding = contentPadding,
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
    @DrawableRes icon: Int? = null,
    @DrawableRes trailingIcon: Int? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    style: TextStyle = ZashiButtonDefaults.style,
    shape: Shape = ZashiButtonDefaults.shape,
    contentPadding: PaddingValues = ZashiButtonDefaults.contentPadding,
    colors: ZashiButtonColors = LocalZashiButtonColors.current ?: ZashiButtonDefaults.primaryColors(),
    content: @Composable RowScope.(ZashiButtonScope) -> Unit = ZashiButtonDefaults.content
) {
    val scope =
        object : ZashiButtonScope {
            @Composable
            override fun LeadingIcon() {
                if (icon != null) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
            }

            @Composable
            override fun TrailingIcon() {
                if (trailingIcon != null) {
                    Image(
                        painter = painterResource(trailingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
            }

            @Composable
            override fun Text() {
                Text(
                    text = text,
                    style = style,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            @Composable
            override fun Loading() {
                if (isLoading) {
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

    val borderColor = if (enabled) colors.borderColor else colors.disabledBorderColor

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        contentPadding = contentPadding,
        enabled = enabled,
        colors = colors.toButtonColors(),
        border = borderColor.takeIf { it != Color.Unspecified }?.let { BorderStroke(1.dp, it) },
        content = {
            content(scope)
        }
    )
}

interface ZashiButtonScope {
    @Composable
    fun LeadingIcon()

    @Composable
    fun TrailingIcon()

    @Composable
    fun Text()

    @Composable
    fun Loading()
}

object ZashiButtonDefaults {
    val content: @Composable RowScope.(ZashiButtonScope) -> Unit
        get() = { scope ->
            scope.LeadingIcon()
            Spacer(modifier = Modifier.width(6.dp))
            scope.Text()
            Spacer(modifier = Modifier.width(6.dp))
            scope.TrailingIcon()
            scope.Loading()
        }

    val style: TextStyle
        @Composable get() = ZashiTypography.textMd

    val contentPadding: PaddingValues
        get() = PaddingValues(horizontal = 10.dp)

    val shape: Shape
        get() = RoundedCornerShape(12.dp)

    @Composable
    fun primaryColors(
        source: ZashiColorsInternal = ZashiColors,
        containerColor: Color = source.Btns.Primary.btnPrimaryBg,
        contentColor: Color = source.Btns.Primary.btnPrimaryFg,
        disabledContainerColor: Color = source.Btns.Primary.btnPrimaryBgDisabled,
        disabledContentColor: Color = source.Btns.Primary.btnBoldFgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = Color.Unspecified,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        disabledBorderColor = Color.Unspecified
    )

    @Composable
    fun secondaryColors(
        source: ZashiColorsInternal = ZashiColors,
        containerColor: Color = source.Btns.Secondary.btnSecondaryBg,
        contentColor: Color = source.Btns.Secondary.btnSecondaryFg,
        borderColor: Color = Color.Unspecified,
        disabledContainerColor: Color = source.Btns.Secondary.btnSecondaryBgDisabled,
        disabledContentColor: Color = source.Btns.Secondary.btnSecondaryFgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = borderColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        disabledBorderColor = Color.Unspecified
    )

    @Composable
    fun tertiaryColors(
        source: ZashiColorsInternal = ZashiColors,
        containerColor: Color = source.Btns.Tertiary.btnTertiaryBg,
        contentColor: Color = source.Btns.Tertiary.btnTertiaryFg,
        disabledContainerColor: Color = source.Btns.Tertiary.btnTertiaryBgDisabled,
        disabledContentColor: Color = source.Btns.Tertiary.btnTertiaryFgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        borderColor = Color.Unspecified,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        disabledBorderColor = Color.Unspecified
    )

    @Composable
    fun destructive1Colors(
        source: ZashiColorsInternal = ZashiColors,
        containerColor: Color = source.Btns.Destructive1.btnDestroy1Bg,
        contentColor: Color = source.Btns.Destructive1.btnDestroy1Fg,
        borderColor: Color = source.Btns.Destructive1.btnDestroy1Border,
        disabledContainerColor: Color = source.Btns.Destructive1.btnDestroy1BgDisabled,
        disabledContentColor: Color = source.Btns.Destructive1.btnDestroy1FgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        borderColor = borderColor,
        disabledBorderColor = Color.Unspecified
    )

    @Composable
    fun destructive2Colors(
        source: ZashiColorsInternal = ZashiColors,
        containerColor: Color = source.Btns.Destructive2.btnDestroy2Bg,
        contentColor: Color = source.Btns.Destructive2.btnDestroy2Fg,
        borderColor: Color = Color.Unspecified,
        disabledContainerColor: Color = source.Btns.Destructive2.btnDestroy2BgDisabled,
        disabledContentColor: Color = source.Btns.Destructive2.btnDestroy2FgDisabled,
    ) = ZashiButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        borderColor = borderColor,
        disabledBorderColor = Color.Unspecified
    )
}

@Immutable
data class ZashiButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val disabledBorderColor: Color,
)

@Immutable
data class ButtonState(
    val text: StringResource,
    @DrawableRes val icon: Int? = null,
    @DrawableRes val trailingIcon: Int? = null,
    val isEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val onClick: () -> Unit = {},
)

@Composable
fun ZashiButtonColors.toButtonColors() =
    ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

@Suppress("CompositionLocalAllowlist")
val LocalZashiButtonColors =
    compositionLocalOf<ZashiButtonColors?> {
        null
    }

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

@PreviewScreens
@Composable
private fun PrimaryWithIconPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Primary",
                icon = android.R.drawable.ic_secure,
                onClick = {},
            )
        }
    }

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

@PreviewScreens
@Composable
private fun SmallWidthPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiButton(
                modifier = Modifier.wrapContentWidth(),
                text = "Small Width Button",
                colors = ZashiButtonDefaults.destructive1Colors(),
                onClick = {},
            )
        }
    }
