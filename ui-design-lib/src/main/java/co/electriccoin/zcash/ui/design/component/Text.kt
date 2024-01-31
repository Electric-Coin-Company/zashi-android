@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ReferenceComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Column {
                Reference(
                    text = "Test reference text",
                    onClick = {},
                    modifier = Modifier.padding(all = ZcashTheme.dimens.spacingDefault)
                )
                Reference(
                    text = "Reference with icon",
                    imageVector = Icons.Outlined.AccountBox,
                    onClick = {},
                    modifier = Modifier.padding(all = ZcashTheme.dimens.spacingDefault)
                )
                Reference(
                    text = "Normal font weight reference",
                    onClick = {},
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(all = ZcashTheme.dimens.spacingDefault)
                )
            }
        }
    }
}

@Preview
@Composable
private fun StyledBalanceComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Column {
                StyledBalance(
                    mainPart = "1,234.567",
                    secondPart = "89012",
                    textStyles =
                        Pair(
                            ZcashTheme.extendedTypography.balanceStyles.first,
                            ZcashTheme.extendedTypography.balanceStyles.second
                        ),
                    modifier = Modifier
                )
            }
        }
    }
}

// Add previews for the rest of the composables

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
        modifier = modifier,
        style = ZcashTheme.typography.secondary.headlineLarge,
    )
}

@Composable
fun SubHeader(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = ZcashTheme.colors.onBackgroundHeader,
) {
    Text(
        text = text,
        color = color,
        textAlign = textAlign,
        modifier = modifier,
        style = ZcashTheme.typography.secondary.headlineSmall,
    )
}

@Composable
@Suppress("LongParameterList")
fun BodySmall(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
@Suppress("LongParameterList")
fun Body(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    textFontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = ZcashTheme.typography.primary.bodyLarge,
        fontWeight = textFontWeight
    )
}

@Composable
@Suppress("LongParameterList")
fun TitleLarge(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
@Suppress("LongParameterList")
fun Small(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
@Suppress("LongParameterList")
fun Tiny(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
    )
}

@Composable
fun ListItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ZcashTheme.extendedTypography.listItem,
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
        style = ZcashTheme.extendedTypography.listItem,
        color = ZcashTheme.colors.onBackgroundHeader,
        modifier = modifier
    )
}

@Suppress("LongParameterList")
@Composable
fun Reference(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textAlign: TextAlign = TextAlign.Center,
    imageVector: ImageVector? = null,
    imageContentDescription: String? = null
) {
    Row(
        modifier =
            Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(ZcashTheme.dimens.topAppBarActionRippleCorner))
                .clickable { onClick() }
                .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        imageVector?.let {
            Icon(
                imageVector = imageVector,
                contentDescription = imageContentDescription
            )
        }
        Spacer(modifier = Modifier.padding(ZcashTheme.dimens.spacingTiny))
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style =
                ZcashTheme.typography.primary.bodyLarge
                    .merge(
                        TextStyle(
                            color = ZcashTheme.colors.reference,
                            textAlign = textAlign,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = fontWeight
                        )
                    )
        )
    }
}

/**
 * Pass amount of Zcash tokens you want to display and the component style it according to the design requirements.
 *
 * @param mainPart of Zcash tokens to be displayed in a bigger font style
 * @param secondPart of Zcash tokens to be displayed in a smaller font style
 * @param modifier to modify the Text UI element as needed
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StyledBalance(
    mainPart: String,
    secondPart: String,
    textStyles: Pair<TextStyle, TextStyle>,
    modifier: Modifier = Modifier
) {
    val content =
        buildAnnotatedString {
            withStyle(
                style = textStyles.first.toSpanStyle()
            ) {
                append(mainPart)
            }
            withStyle(
                style = textStyles.second.toSpanStyle()
            ) {
                append(secondPart)
            }
        }

    Text(
        text = content,
        // fixme color
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        modifier =
            Modifier
                .basicMarquee()
                .then(modifier)
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

@Composable
fun NavigationTabText(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ZcashTheme.extendedTypography.textNavTab,
        fontWeight =
            if (selected) {
                FontWeight.Black
            } else {
                FontWeight.Normal
            },
        maxLines = 1,
        overflow = TextOverflow.Visible,
        color = ZcashTheme.colors.tabTextColor,
        modifier = modifier
    )
}
