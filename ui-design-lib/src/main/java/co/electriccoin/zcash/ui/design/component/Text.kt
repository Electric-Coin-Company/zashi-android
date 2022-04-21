package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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

@Composable
fun Reference(
    text: String,
    tag: String,
    link: String,
    modifier: Modifier = Modifier,
    onClick: (link: String) -> Unit
) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(
            tag = tag,
            annotation = link
        )
        withStyle(style = SpanStyle(color = ZcashTheme.colors.reference)) {
            append(text)
        }
        pop()
    }
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = tag, start = offset, end = offset).firstOrNull()?.let {
                onClick(it.item)
            }
        }
    )
}
