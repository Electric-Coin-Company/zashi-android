@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.MonetarySeparators
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import java.util.Locale

@Preview
@Composable
private fun ReferenceComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
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
        BlankSurface {
            Column {
                StyledBalance(
                    balanceString = "1,234.56789012",
                    textStyles =
                        Pair(
                            ZcashTheme.extendedTypography.balanceWidgetStyles.first,
                            ZcashTheme.extendedTypography.balanceWidgetStyles.second
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
    color: Color = ZcashTheme.colors.textPrimary,
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
    color: Color = ZcashTheme.colors.textPrimary,
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
    textFontWeight: FontWeight = FontWeight.Normal,
    color: Color = ZcashTheme.colors.textPrimary,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = textFontWeight
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
    color: Color = ZcashTheme.colors.textPrimary,
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
    color: Color = ZcashTheme.colors.textPrimary,
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
    textFontWeight: FontWeight = FontWeight.Normal,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = ZcashTheme.colors.textPrimary,
) {
    Text(
        text = text,
        color = color,
        fontWeight = textFontWeight,
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
    color: Color = ZcashTheme.colors.textPrimary,
) {
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
@Suppress("LongParameterList")
fun TextWithIcon(
    text: String,
    iconVector: ImageVector,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    style: TextStyle = LocalTextStyle.current,
    color: Color = ZcashTheme.colors.textPrimary,
) {
    Row(
        modifier =
            Modifier
                .wrapContentSize()
                .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = iconVector,
            contentDescription = iconContentDescription
        )

        Spacer(modifier = Modifier.padding(3.dp))

        Text(
            text = text,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign,
            style = style,
        )
    }
}

@Suppress("LongParameterList")
@Composable
fun Reference(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = ZcashTheme.typography.primary.bodyLarge,
    color: Color = ZcashTheme.colors.reference,
    imageVector: ImageVector? = null,
    imageContentDescription: String? = null
) {
    Row(
        modifier =
            Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
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
                textStyle.merge(
                    TextStyle(
                        color = color,
                        textAlign = textAlign,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = fontWeight
                    )
                )
        )
    }
}

/**
 * This accepts string with balance and displays it in the UI component styled according to the design
 * requirements. The function displays the balance within two parts.
 *
 * @param balanceString String of Zcash formatted balance
 * @param textStyles Styles for the first and second part of the balance
 * @param textColor Optional color to modify the default font color from [textStyles]
 * @param modifier Modifier to modify the Text UI element as needed
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StyledBalance(
    balanceString: String,
    textStyles: Pair<TextStyle, TextStyle>,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
    prefix: String? = null
) {
    val balanceSplit = splitBalance(balanceString, prefix)

    val content =
        buildAnnotatedString {
            withStyle(
                style = textStyles.first.toSpanStyle()
            ) {
                append(balanceSplit.first)
            }
            withStyle(
                style = textStyles.second.toSpanStyle()
            ) {
                append(balanceSplit.second)
            }
        }

    if (textColor != null) {
        Text(
            text = content,
            color = textColor,
            maxLines = 1,
            modifier =
                Modifier
                    .basicMarquee()
                    .then(modifier)
        )
    } else {
        Text(
            text = content,
            maxLines = 1,
            modifier =
                Modifier
                    .basicMarquee()
                    .then(modifier)
        )
    }
}

private fun splitBalance(
    balance: String,
    prefix: String?
): Pair<String, String> {
    Twig.debug { "Balance before split: $balance, prefix: $prefix" }

    @Suppress("MagicNumber")
    val cutPosition = balance.indexOf(MonetarySeparators.current(Locale.getDefault()).decimal) + 4
    val firstPart =
        (prefix ?: "") +
            balance.substring(
                startIndex = 0,
                endIndex = cutPosition
            )
    val secondPart =
        balance.substring(
            startIndex = cutPosition
        )

    Twig.debug { "Balance after split: $firstPart|$secondPart" }

    return Pair(firstPart, secondPart)
}

@Composable
fun BodyWithFiatCurrencySymbol(
    amount: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = amount,
        style = MaterialTheme.typography.bodyLarge,
        color = ZcashTheme.colors.textPrimary,
        modifier = modifier
    )
}

@Preview
@Composable
private fun NavigationTabTextPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Column {
                NavigationTabText(
                    text = "Account",
                    selected = false,
                    modifier = Modifier,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun NavigationTabText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ZcashTheme.extendedTypography.textNavTab,
        textAlign = TextAlign.Center,
        fontWeight =
            if (selected) {
                FontWeight.Black
            } else {
                FontWeight.Normal
            },
        maxLines = 1,
        overflow = TextOverflow.Visible,
        color = ZcashTheme.colors.textPrimary,
        modifier =
            Modifier
                .clip(RoundedCornerShape(ZcashTheme.dimens.regularRippleEffectCorner))
                .clickable { onClick() }
                .then(modifier)
    )
}
