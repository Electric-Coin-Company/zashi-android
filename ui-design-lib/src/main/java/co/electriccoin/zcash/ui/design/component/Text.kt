package co.electriccoin.zcash.ui.design.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
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
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
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
        color = MaterialTheme.colorScheme.onBackground,
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
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}
