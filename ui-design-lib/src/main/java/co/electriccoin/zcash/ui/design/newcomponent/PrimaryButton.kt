package co.electriccoin.zcash.ui.design.newcomponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun PrimaryButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    shape: Shape = PrimaryButtonDefaults.Shape,
    colors: ButtonColors = PrimaryButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = PrimaryButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PrimaryButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    PrimaryButton(
        onClick = state.onClick,
        modifier = modifier,
        enabled = state.isEnabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        PrimaryButtonText(state.text)
    }
}

@Suppress("LongParameterList")
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = PrimaryButtonDefaults.Shape,
    colors: ButtonColors = PrimaryButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PrimaryButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) = Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content,
)

@Composable
fun PrimaryButtonText(text: StringResource) {
    Text(
        text = text.getValue(),
        style = ZcashTheme.typography.secondary.headlineMedium,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
    )
}

@ScreenPreview
@Composable
private fun ButtonPreview() = ZcashTheme {
    PrimaryButton(state = ButtonState(text = stringRes("Button text")))
}

@ScreenPreview
@Composable
private fun ButtonDisabledPreview() = ZcashTheme {
    PrimaryButton(state = ButtonState(text = stringRes("Button text"), isEnabled = false))
}

object PrimaryButtonDefaults {

    val Shape get() = RoundedCornerShape(10.dp)

    val ContentPadding get() = PaddingValues(vertical = 14.dp, horizontal = 20.dp)

    @Composable
    fun buttonColors(
        containerColor: Color = Color(0xFF252627),
        contentColor: Color = Color(0xFFFFFFFF),
        disabledContainerColor: Color = Color(0xFFEAEAEB),
        disabledContentColor: Color = Color(0xFF979899),
    ) = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun buttonElevation(
        defaultElevation: Dp = 2.dp,
    ) = ButtonDefaults.buttonElevation(defaultElevation = defaultElevation)
}
