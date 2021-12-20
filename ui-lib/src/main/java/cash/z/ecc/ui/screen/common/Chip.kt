package cash.z.ecc.ui.screen.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.ui.screen.onboarding.model.Index
import cash.z.ecc.ui.theme.ZcashTheme

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = false) {
        Chip(Index(0), "edict")
    }
}

@Composable
fun Chip(
    index: Index,
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.then(Modifier.padding(4.dp)),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.secondary,
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = (index.value + 1).toString(),
                style = ZcashTheme.typography.chipIndex,
                color = ZcashTheme.colors.chipIndex,
            )
            Spacer(modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.testTag(CommonTag.CHIP)
            )
        }
    }
}
