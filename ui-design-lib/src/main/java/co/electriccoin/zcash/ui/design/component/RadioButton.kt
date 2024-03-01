package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        RadioButton(
            text = "test",
            selected = true,
            onClick = {},
            modifier = Modifier
        )
    }
}

@Composable
fun RadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
) {
    Row(
        modifier =
            Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                .clickable { onClick() }
                .then(
                    if (testTag != null) {
                        Modifier.testTag(testTag)
                    } else {
                        Modifier
                    }
                )
                .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = ZcashTheme.colors.radioButtonColor,
                    unselectedColor = ZcashTheme.colors.radioButtonColor,
                )
        )
        Text(
            text = text,
            style = ZcashTheme.extendedTypography.radioButton,
            color = ZcashTheme.colors.radioButtonTextColor,
            modifier =
                Modifier.padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 0.dp,
                    end = ZcashTheme.dimens.spacingDefault
                )
        )
    }
}
