package cash.z.ecc.sdk.ext.ui

import android.content.Context
import cash.z.ecc.sdk.model.MonetarySeparators

object ZecStringExt {

    /**
     * Builds regex with current local monetary separators for continuous input checking.
     *
     * Regex example: ^([0-9]*([0-9]+([,]$|[,][0-9]+))*([.]$|[.][0-9]+)?)?$
     * Inputs may differ according to user locale.
     *
     * Valid amounts: "" . .123 123, 123. 123,456 123.456 123,456.789 123,456,789 123,456,789.123 etc.
     * Invalid amounts: 123,, 123,. 123.. .123 ,123 123.456.789 etc.
     *
     * @param context used for loading localized pattern from strings.xml
     * @param separators which consist of localized monetary separators
     * @param zecString to be validated
     *
     * @return true in case of validation success, false otherwise
     */
    fun filterContinuous(context: Context, separators: MonetarySeparators, zecString: String): Boolean {
        return context.getString(
            R.string.zec_amount_regex_filter,
            separators.grouping,
            separators.decimal
        ).toRegex().matches(zecString)
    }
}
