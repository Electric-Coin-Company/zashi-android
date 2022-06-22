package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
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
fun Small(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
        textAlign = textAlign
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

@Composable
fun Reference(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ClickableText(
        text = AnnotatedString(text),
        style = MaterialTheme.typography.bodyLarge
            .merge(TextStyle(color = ZcashTheme.colors.reference)),
        modifier = modifier,
        onClick = {
            onClick()
        }
    )
}

@Composable
fun HeaderWithZecIcon(
    text: String,
    modifier: Modifier = Modifier
) {
    // needs to be done differently - to have the icon aligned exact top of the text
    Row (modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_zcash_currency_symbol),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun BodyWithDollarIcon(
    text: String,
    modifier: Modifier = Modifier
) {
    Row {
        // needs a new dollar icon
        Icon(
            painter = painterResource(R.drawable.ic_dollar_currency_symbol),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
        )
    }
}
