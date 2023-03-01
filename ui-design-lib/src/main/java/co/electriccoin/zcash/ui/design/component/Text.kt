package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ZcashTheme.colors.onBackgroundHeader
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        color = color,
        modifier = modifier
    )
}

@Composable
fun Body(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        text = text,
        textAlign = textAlign,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        modifier = modifier
    )
}

@Composable
fun Small(
    text: String,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
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

/**
 * Pass amount of ZECs you want to display and the component appends ZEC symbol to it. We're using
 * a custom font here, which is Roboto modified to replace the dollar symbol with the ZEC symbol internally.
 *
 * @param amount of ZECs to be displayed
 * @param modifier to modify the Text UI element as needed
 */
@Composable
fun HeaderWithZecIcon(
    amount: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.amount_with_zec_currency_symbol, amount),
        style = ZcashTheme.typography.zecBalance,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
fun BodyWithFiatCurrencySymbol(
    amount: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = amount,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}
