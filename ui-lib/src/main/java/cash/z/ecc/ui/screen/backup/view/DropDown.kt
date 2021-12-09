package cash.z.ecc.ui.screen.backup.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cash.z.ecc.ui.screen.backup.BackupTag
import cash.z.ecc.ui.screen.onboarding.model.Index
import cash.z.ecc.ui.theme.MINIMAL_WEIGHT
import cash.z.ecc.ui.theme.ZcashTheme

/**
 * @param chipIndex The index of the chip, which is displayed to the user.
 * @param dropdownText Text to display when the drop down is not open.
 * @param choices Item choices to display in the open drop down menu.  Positional index is important.
 * @param onChoiceSelected Callback with the positional index of the item the user selected from [choices].
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipDropDown(
    chipIndex: Index,
    dropdownText: String,
    choices: List<String>,
    modifier: Modifier = Modifier,
    onChoiceSelected: (Index) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.then(Modifier.padding(4.dp)),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.secondary,
        elevation = 8.dp,
        onClick = { expanded = !expanded }
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = (chipIndex.value + 1).toString(),
                style = ZcashTheme.typography.chipIndex,
                color = ZcashTheme.colors.chipIndex,
            )
            Spacer(modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.dp))
            Text(dropdownText)
            Spacer(modifier = modifier.fillMaxWidth(MINIMAL_WEIGHT))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
        val dropdownModifier = if (expanded) {
            Modifier.testTag(BackupTag.DROPDOWN_MENU)
        } else {
            Modifier
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = dropdownModifier
        ) {
            choices.forEachIndexed { index, label ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onChoiceSelected(Index(index))
                }) {
                    Text(text = label)
                }
            }
        }
    }
}
