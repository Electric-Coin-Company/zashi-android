package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ComposablePreview() {
    val checkBoxState = remember { mutableStateOf(false) }
    ZcashTheme(forceDarkMode = false) {
        CheckBox(
            onCheckedChange = { checkBoxState.value = it },
            text = "test",
            checked = checkBoxState.value,
            checkBoxTestTag = null
        )
    }
}

@Composable
fun CheckBox(
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    checkBoxTestTag: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val checkBoxModifier = Modifier
            .padding(
                top = ZcashTheme.dimens.spacingTiny,
                bottom = ZcashTheme.dimens.spacingTiny,
                end = ZcashTheme.dimens.spacingTiny
            )
            .then(
                if (checkBoxTestTag != null) {
                    Modifier.testTag(checkBoxTestTag)
                } else {
                    Modifier
                }
            )
        val (checkedState, setCheckedState) = rememberSaveable { mutableStateOf(checked) }
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                setCheckedState(it)
                onCheckedChange(it)
            },
            enabled = true,
            modifier = checkBoxModifier
        )
        ClickableText(
            onClick = {
                setCheckedState(!checkedState)
                onCheckedChange(!checkedState)
            },
            text = AnnotatedString(text),
            style = ZcashTheme.extendedTypography.checkboxText
        )
    }
}
