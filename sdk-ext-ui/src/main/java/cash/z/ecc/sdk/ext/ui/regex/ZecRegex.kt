package cash.z.ecc.sdk.ext.ui.regex

import android.content.Context
import cash.z.ecc.sdk.ext.ui.R
import cash.z.ecc.sdk.model.MonetarySeparators

object ZecRegex {

    /**
     * Builds regex with current local monetary separators for continuous input checking.
     *
     * Regex example: ^^$|[0-9]+([,]$|[,][0-9]+)?([.]$|[.][0-9]+)?$
     * Inputs may differ according to user locale.
     *
     * Valid amounts: "" 123 123, 123. 123,456 123.456 123,456.789 etc.
     * Invalid amounts: 123,, 123,. 123.. .123 ,123 123,456,789 123.456.789 etc.
     *
     * @param context used for loading localized pattern from strings.xml
     * @param separators which consist of localized monetary separators
     *
     * @return regex with pattern for entered ZEC amount
     */
    fun getZecAmountContinuousFilter(context: Context, separators: MonetarySeparators): Regex {
        return context.getString(
            R.string.zec_amount_regex_filter,
            separators.grouping,
            separators.grouping,
            separators.decimal,
            separators.decimal
        ).toRegex()
    }
}
