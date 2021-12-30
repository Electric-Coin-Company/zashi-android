package cash.z.ecc.ui.screen.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cash.z.ecc.ui.theme.ZcashTheme

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h1,
        color = ZcashTheme.colors.onBackgroundHeader,
        modifier = modifier
    )
}

@Composable
fun Body(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onBackground,
        modifier = modifier
    )
}

@Composable
fun ListItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ZcashTheme.typography.listItem,
        color = MaterialTheme.colors.onBackground,
        modifier = modifier
    )
}

@Composable
fun ListHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ZcashTheme.typography.listItem,
        color = MaterialTheme.colors.onBackground,
        modifier = modifier
    )
}
