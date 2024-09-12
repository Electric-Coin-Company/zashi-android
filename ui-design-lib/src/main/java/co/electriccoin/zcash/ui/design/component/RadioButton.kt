package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Suppress("LongParameterList", "LongMethod")
@Composable
fun RadioButton(
    state: RadioButtonState,
    modifier: Modifier = Modifier,
    checkedContent: @Composable () -> Unit = { RadioButtonCheckedContent(state) },
    uncheckedContent: @Composable () -> Unit = { RadioButtonUncheckedContent(state) },
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    testTag: String? = null,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    indication = rememberRipple(),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = state.onClick,
                    role = Role.Button,
                )
                .padding(horizontal = 20.dp)
                .then(
                    if (testTag != null) {
                        Modifier.testTag(testTag)
                    } else {
                        Modifier
                    }
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButtonIndicator(
                state = state,
                checkedContent = checkedContent,
                uncheckedContent = uncheckedContent
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = state.text.getValue(),
                    style = ZcashTheme.extendedTypography.radioButton,
                    color = ZcashTheme.colors.textPrimary,
                    modifier =
                        Modifier.padding(
                            top = 14.dp,
                            bottom = if (state.subtitle == null) 14.dp else 0.dp,
                            start = 0.dp,
                            end = ZcashTheme.dimens.spacingDefault
                        )
                )

                if (state.subtitle != null) {
                    Text(
                        text = state.subtitle.getValue(),
                        style = ZcashTheme.extendedTypography.radioButton,
                        fontWeight = FontWeight.Normal,
                        color = ZcashTheme.zashiColors.textTertiary,
                        modifier =
                            Modifier.padding(
                                bottom = 6.dp,
                                start = 0.dp,
                                end = ZcashTheme.dimens.spacingDefault
                            )
                    )
                }
            }
        }
        if (trailingContent != null) {
            Row {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }
        }
    }
}

@Composable
fun RadioButtonUncheckedContent(state: RadioButtonState) {
    Image(
        painter = painterResource(id = R.drawable.ic_radio_button_unchecked),
        contentDescription = state.text.getValue(),
    )
}

@Composable
fun RadioButtonCheckedContent(state: RadioButtonState) {
    Image(
        painter = painterResource(id = R.drawable.ic_radio_button_checked),
        contentDescription = state.text.getValue(),
    )
}

@Composable
private fun RadioButtonIndicator(
    state: RadioButtonState,
    checkedContent: @Composable () -> Unit,
    uncheckedContent: @Composable () -> Unit
) {
    Box {
        uncheckedContent()
        AnimatedVisibility(
            visible = state.isChecked,
            enter = scaleIn(spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy)),
            exit = scaleOut(spring(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioMediumBouncy))
        ) {
            checkedContent()
        }
    }
}

data class RadioButtonState(
    val text: StringResource,
    val isChecked: Boolean,
    val subtitle: StringResource? = null,
    val onClick: () -> Unit,
)

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun RadioButtonPreview() =
    ZcashTheme {
        BlankBgColumn {
            var isChecked by remember { mutableStateOf(false) }

            RadioButton(
                modifier = Modifier.fillMaxWidth(),
                state =
                    RadioButtonState(
                        text = stringRes("test"),
                        isChecked = isChecked,
                        onClick = { isChecked = !isChecked },
                    ),
                trailingContent = {
                    Text(text = "Trailing text")
                }
            )
            RadioButton(
                state =
                    RadioButtonState(
                        text = stringRes("test"),
                        isChecked = true,
                        onClick = {},
                    ),
            )
        }
    }
