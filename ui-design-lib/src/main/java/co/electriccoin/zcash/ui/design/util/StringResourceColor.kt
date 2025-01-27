package co.electriccoin.zcash.ui.design.util

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.StringResourceColor.DEFAULT
import co.electriccoin.zcash.ui.design.util.StringResourceColor.NEGATIVE
import co.electriccoin.zcash.ui.design.util.StringResourceColor.POSITIVE

enum class StringResourceColor {
    DEFAULT,
    POSITIVE,
    NEGATIVE
}

@Composable
fun StringResourceColor.getColor() =
    when (this) {
        DEFAULT -> ZashiColors.Text.textPrimary
        POSITIVE -> ZashiColors.Utility.SuccessGreen.utilitySuccess700
        NEGATIVE -> ZashiColors.Text.textError
    }
