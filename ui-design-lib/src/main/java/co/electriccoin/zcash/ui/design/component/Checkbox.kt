package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ComposablePreview() {
    val checkBoxState = remember { mutableStateOf(false) }
    ZcashTheme(darkTheme = false) {
        CheckBox(text = "test", onCheckedChange = { checkBoxState.value = it }, checked = checkBoxState.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBox(
    onCheckedChange: ((Boolean) -> Unit),
    text: String,
    modifier: Modifier = Modifier,
    checked: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = true,
                modifier = Modifier.padding(
                    top = ZcashTheme.dimens.spacingTiny,
                    bottom = ZcashTheme.dimens.spacingTiny,
                    end = ZcashTheme.dimens.spacingTiny
                )
            )
        }
        Text(
            text = text,
            style = ZcashTheme.extendedTypography.checkboxText
        )
    }
}
