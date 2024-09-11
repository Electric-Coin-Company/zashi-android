package co.electriccoin.zcash.ui.screen.exchangerate

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.LottieProgress
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue

@Composable
internal fun ZashiButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ZashiButtonDefaults.primaryButtonColors(),
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
internal fun ZashiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    colors: ButtonColors = ZashiButtonDefaults.primaryButtonColors(),
    content: @Composable RowScope.(ZashiButtonScope) -> Unit = ZashiButtonDefaults.content
) {
    val scope =
        object : ZashiButtonScope {
            @Composable
            override fun Text() {
                Text(text = text)
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
        colors = colors,
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
