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
import androidx.compose.ui.text.style.TextOverflow
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.Typography

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = ZcashTheme.colors.onBackgroundHeader,
) {
    Text(
        text = text,
        color = color,
        textAlign = textAlign,
        style = Typography.headlineLarge,
        modifier = modifier
    )
}

@Composable
fun Body(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        text = text,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        style = Typography.bodyLarge,
        color = color,
        modifier = modifier
    )
}

@Composable
fun BodyMedium(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        style = Typography.bodyMedium,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun BodyMedium(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = Typography.bodyMedium,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun BodySmall(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        style = Typography.bodySmall,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun BalanceText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        style = Typography.headlineMedium,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun TitleMedium(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = ZcashTheme.colors.onBackgroundHeader,
) {
    Text(
        text = text,
        style = Typography.titleMedium,
        color = color,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun TitleLarge(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = ZcashTheme.colors.onBackgroundHeader,
) {
    Text(
        text = text,
        style = Typography.titleLarge,
        color = color,
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
        color = ZcashTheme.colors.onBackgroundHeader,
        modifier = modifier
    )
}

@Composable
fun Reference(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit
) {
    ClickableText(
        text = AnnotatedString(text),
        style = style
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
