package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.then(Modifier.padding(4.dp)),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = (index.value + 1).toString(),
                style = ZcashTheme.typography.chipIndex,
                color = ZcashTheme.colors.chipIndex
            )
            Spacer(modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.testTag(CommonTag.CHIP)
            )
        }
    }
}

@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(8.dp),
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
