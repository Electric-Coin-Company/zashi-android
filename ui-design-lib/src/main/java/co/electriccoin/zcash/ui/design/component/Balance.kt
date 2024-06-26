package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.MonetarySeparators
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import java.util.Locale

@Preview
@Composable
private fun StyledBalanceComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Column {
                StyledBalance(
                    balanceParts = ZecAmountTriple(main = "1,234.56789012"),
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

/**
 * This accepts string with balance and displays it in the UI component styled according to the design
 * requirements. The function displays the balance within two parts.
 *
 * @param balanceParts [ZecAmountTriple] class that holds balance parts
 * @param textStyles Styles for the first and second part of the balance
 * @param textColor Optional color to modify the default font color from [textStyles]
 * @param modifier Modifier to modify the Text UI element as needed
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StyledBalance(
    balanceParts: ZecAmountTriple,
    textStyles: Pair<TextStyle, TextStyle>,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
) {
    val balanceSplit = splitBalance(balanceParts)

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

private const val CUT_POSITION_OFFSET = 4

private fun splitBalance(balanceStringParts: ZecAmountTriple): Pair<String, String> {
    Twig.debug { "Balance parts before calculation: $balanceStringParts" }

    val cutPosition =
        balanceStringParts.main.indexOf(
            startIndex = 0,
            char = MonetarySeparators.current(Locale.getDefault()).decimal,
            ignoreCase = true
        ).let { separatorPosition ->
            if (separatorPosition + CUT_POSITION_OFFSET < balanceStringParts.main.length) {
                separatorPosition + CUT_POSITION_OFFSET
            } else {
                balanceStringParts.main.length
            }
        }

    val firstPart =
        buildString {
            append(balanceStringParts.prefix ?: "")
            append(
                balanceStringParts.main.substring(
                    startIndex = 0,
                    endIndex = cutPosition
                )
            )
            append(balanceStringParts.suffix ?: "")
        }

    val secondPart =
        balanceStringParts.main.substring(
            startIndex = cutPosition
        )

    Twig.debug { "Balance after split: $firstPart|$secondPart" }

    return Pair(firstPart, secondPart)
}

data class ZecAmountTriple(
    val main: String,
    val prefix: String? = null,
    val suffix: String? = null
)
