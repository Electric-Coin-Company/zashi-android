package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.rememberDesiredFormatLocale
import java.text.DecimalFormatSymbols
import java.util.Locale

@Preview
@Composable
private fun StyledBalancePreview() =
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Column {
                StyledBalance(
                    balanceParts = ZecAmountTriple(main = "1,234.56789012"),
                    isHideBalances = false,
                    modifier = Modifier
                )
            }
        }
    }

@Preview
@Composable
private fun HiddenStyledBalancePreview() =
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Column {
                StyledBalance(
                    balanceParts = ZecAmountTriple(main = "1,234.56789012"),
                    isHideBalances = true,
                    modifier = Modifier
                )
            }
        }
    }

/**
 * This accepts string with balance and displays it in the UI component styled according to the design
 * requirements. The function displays the balance within two parts.
 *
 * @param balanceParts [ZecAmountTriple] class that holds balance parts
 * @param isHideBalances Flag referring about the balance being hidden or not
 * @param hiddenBalancePlaceholder String holding the placeholder for the hidden balance
 * @param textStyle Styles for the integer and floating part of the balance
 * @param textColor Optional color to modify the default font color from [textStyle]
 * @param modifier Modifier to modify the Text UI element as needed
 */
@Suppress("LongParameterList")
@Composable
fun StyledBalance(
    balanceParts: ZecAmountTriple,
    modifier: Modifier = Modifier,
    isHideBalances: Boolean = false,
    showDust: Boolean = true,
    hiddenBalancePlaceholder: String = stringResource(id = R.string.hide_balance_placeholder),
    textColor: Color = Color.Unspecified,
    textStyle: BalanceTextStyle = StyledBalanceDefaults.textStyles(),
) {
    val locale = rememberDesiredFormatLocale()
    val content =
        if (isHideBalances) {
            buildAnnotatedString {
                withStyle(
                    style = textStyle.mostSignificantPart.toSpanStyle()
                ) {
                    append(hiddenBalancePlaceholder)
                }
            }
        } else {
            val balanceSplit = splitBalance(balanceParts, locale)

            buildAnnotatedString {
                withStyle(
                    style = textStyle.mostSignificantPart.toSpanStyle()
                ) {
                    append(balanceSplit.first)
                }
                if (showDust) {
                    withStyle(
                        style = textStyle.leastSignificantPart.toSpanStyle()
                    ) {
                        append(balanceSplit.second)
                    }
                }
            }
        }

    val resultModifier =
        Modifier
            .basicMarquee()
            .then(modifier)

    SelectionContainer {
        Text(
            text = content,
            color = textColor,
            maxLines = 1,
            modifier = resultModifier
        )
    }
}

private const val CUT_POSITION_OFFSET = 4

private fun splitBalance(balanceStringParts: ZecAmountTriple, locale: Locale): Pair<String, String> {
    Twig.debug { "Balance parts before calculation: $balanceStringParts" }

    val cutPosition =
        balanceStringParts.main
            .indexOf(
                startIndex = 0,
                char = DecimalFormatSymbols(locale).monetaryDecimalSeparator,
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

@Immutable
data class BalanceTextStyle(
    val mostSignificantPart: TextStyle,
    val leastSignificantPart: TextStyle
)

object StyledBalanceDefaults {
    @Stable
    @Composable
    fun textStyles(
        mostSignificantPart: TextStyle = ZcashTheme.extendedTypography.balanceWidgetStyles.first,
        leastSignificantPart: TextStyle = ZcashTheme.extendedTypography.balanceWidgetStyles.second,
    ) = BalanceTextStyle(
        mostSignificantPart = mostSignificantPart,
        leastSignificantPart = leastSignificantPart
    )
}
