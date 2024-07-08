package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.MonetarySeparators
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.design.R
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
                    isHideBalances = false,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(24.dp))

                StyledBalance(
                    balanceParts = ZecAmountTriple(main = "1,234.56789012"),
                    isHideBalances = true,
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
 * @param isHideBalances Flag referring about the balance being hidden or not
 * @param hiddenBalancePlaceholder String holding the placeholder for the hidden balance
 * @param textStyle Styles for the integer and floating part of the balance
 * @param textColor Optional color to modify the default font color from [textStyle]
 * @param modifier Modifier to modify the Text UI element as needed
 */
@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongParameterList")
@Composable
fun StyledBalance(
    balanceParts: ZecAmountTriple,
    modifier: Modifier = Modifier,
    isHideBalances: Boolean = false,
    hiddenBalancePlaceholder: String = stringResource(id = R.string.hide_balance_placeholder),
    textColor: Color = Color.Unspecified,
    textStyle: BalanceTextStyle = StyledBalanceDefaults.textStyles(),
) {
    val content =
        if (isHideBalances) {
            buildAnnotatedString {
                withStyle(
                    style = textStyles.first.toSpanStyle()
                ) {
                    append(hiddenBalancePlaceholder)
                }
            }
        } else {
            val balanceSplit = splitBalance(balanceParts)

            buildAnnotatedString {
                withStyle(
                    style = textStyle.integerPart.toSpanStyle()
                ) {
                    append(balanceSplit.first)
                }
                withStyle(
                    style = textStyle.floatingPart.toSpanStyle()
                ) {
                    append(balanceSplit.second)
                }
            }
        }

    val resultModifier =
        Modifier
            .basicMarquee()
            .animateContentSize()
            .then(modifier)

    Text(
        text = content,
        color = textColor,
        maxLines = 1,
        modifier = resultModifier
    )
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

@Immutable
data class BalanceTextStyle(val integerPart: TextStyle, val floatingPart: TextStyle)

object StyledBalanceDefaults {
    @Stable
    @Composable
    fun textStyles(
        integerPart: TextStyle = ZcashTheme.extendedTypography.balanceWidgetStyles.first,
        floatingPart: TextStyle = ZcashTheme.extendedTypography.balanceWidgetStyles.second,
    ) = BalanceTextStyle(
        integerPart = integerPart,
        floatingPart = floatingPart
    )
}
