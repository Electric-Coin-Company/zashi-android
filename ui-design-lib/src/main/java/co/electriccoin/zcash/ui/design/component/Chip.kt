package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ComposableChipPreview() {
    ZcashTheme(forceDarkMode = false) {
        Chip(Index(0), "edict")
    }
}

@Preview
@Composable
private fun ComposableLongChipPreview() {
    ZcashTheme(forceDarkMode = false) {
        Chip(Index(1), "a_very_long_seed_word_that_does_not_fit_into_the_chip_and_thus_needs_to_be_truncated")
    }
}

@Composable
fun Chip(
    index: Index,
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${index.value + 1}. $text",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.then(Modifier.testTag(CommonTag.CHIP))
    )
}

@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RectangleShape,
        modifier = modifier.padding(4.dp),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 8.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .testTag(CommonTag.CHIP)
        )
    }
}
