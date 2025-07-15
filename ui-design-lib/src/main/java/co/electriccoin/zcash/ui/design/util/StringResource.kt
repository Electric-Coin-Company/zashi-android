@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.design.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.ConfigurationCompat
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.Zatoshi
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DateFormat
import java.text.NumberFormat
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.min

@Immutable
sealed interface StringResource {
    @Immutable
    data class ByResource(@StringRes val resource: Int, val args: List<Any>) : StringResource

    @JvmInline
    @Immutable
    value class ByString(val value: String) : StringResource

    @Immutable
    data class ByZatoshi(val zatoshi: Zatoshi, val tickerLocation: TickerLocation) : StringResource

    @Immutable
    data class ByDateTime(val zonedDateTime: ZonedDateTime, val useFullFormat: Boolean) : StringResource

    @Immutable
    data class ByYearMonth(val yearMonth: YearMonth) : StringResource

    @Immutable
    data class ByTransactionId(val transactionId: String, val abbreviated: Boolean) : StringResource

    @Immutable
    data class ByAddress(val address: String, val abbreviated: Boolean) : StringResource

    @Immutable
    data class ByDynamicCurrencyNumber(
        val amount: Number,
        val ticker: String,
        val tickerLocation: TickerLocation
    ) : StringResource

    @Immutable
    data class ByNumber(val number: Number, val minDecimals: Int) : StringResource

    @Immutable
    data class ByDynamicNumber(val number: Number) : StringResource

    operator fun plus(other: StringResource): StringResource = CompositeStringResource(listOf(this, other))

    operator fun plus(other: String): StringResource = CompositeStringResource(listOf(this, stringRes(other)))

    fun isEmpty(): Boolean =
        when (val obj = this) {
            is ByString -> obj.value.isEmpty()
            else -> false
        }
}

@Immutable
private data class CompositeStringResource(val resources: List<StringResource>) : StringResource

@Stable
fun stringRes(@StringRes resource: Int, vararg args: Any): StringResource =
    StringResource.ByResource(resource, args.toList())

@Stable
fun stringRes(value: String): StringResource =
    StringResource.ByString(value)

@Stable
fun stringRes(zatoshi: Zatoshi, tickerLocation: TickerLocation = TickerLocation.AFTER): StringResource =
    StringResource.ByZatoshi(zatoshi, tickerLocation)

@Stable
fun stringResByDynamicCurrencyNumber(
    amount: Number,
    ticker: String,
    tickerLocation: TickerLocation =
        if (ticker == FiatCurrency.USD.symbol) TickerLocation.BEFORE else TickerLocation.AFTER
): StringResource =
    StringResource.ByDynamicCurrencyNumber(
        amount = amount,
        ticker = ticker,
        tickerLocation = tickerLocation
    )

@Stable
fun stringResByDateTime(zonedDateTime: ZonedDateTime, useFullFormat: Boolean): StringResource =
    StringResource.ByDateTime(zonedDateTime, useFullFormat)

@Stable
fun stringRes(yearMonth: YearMonth): StringResource =
    StringResource.ByYearMonth(yearMonth)

@Stable
fun stringResByAddress(value: String, abbreviated: Boolean): StringResource =
    StringResource.ByAddress(value, abbreviated)

@Stable
fun stringResByTransactionId(value: String, abbreviated: Boolean): StringResource =
    StringResource.ByTransactionId(value, abbreviated)

@Stable
fun stringResByNumber(number: Number, minDecimals: Int = 2): StringResource =
    StringResource.ByNumber(number, minDecimals)

@Stable
fun stringResByDynamicNumber(number: Number): StringResource =
    StringResource.ByDynamicNumber(number)

@Stable
@Composable
fun StringResource.getValue(): String =
    getString(
        context = LocalContext.current,
        locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    )

fun StringResource.getString(
    context: Context,
    locale: Locale = ConfigurationCompat.getLocales(context.resources.configuration)[0] ?: Locale.getDefault(),
): String {
    return when (this) {
        is StringResource.ByResource -> convertResource(context)
        is StringResource.ByString -> value
        is StringResource.ByZatoshi -> convertZatoshi()
        is StringResource.ByDynamicCurrencyNumber -> convertDynamicCurrencyNumber(locale)
        is StringResource.ByDateTime -> convertDateTime()
        is StringResource.ByYearMonth -> convertYearMonth()
        is StringResource.ByAddress -> convertAddress()
        is StringResource.ByTransactionId -> convertTransactionId()
        is StringResource.ByNumber -> convertNumber(locale)
        is StringResource.ByDynamicNumber -> convertDynamicNumber(locale)
        is CompositeStringResource -> convertComposite(context, locale)
    }
}

private fun CompositeStringResource.convertComposite(
    context: Context,
    locale: Locale
) = this.resources.joinToString(separator = "") { it.getString(context, locale) }

@Suppress("SpreadOperator")
private fun StringResource.ByResource.convertResource(context: Context) =
    context.getString(
        resource,
        *args.map { if (it is StringResource) it.getString(context) else it }.toTypedArray()
    )

private fun StringResource.ByNumber.convertNumber(locale: Locale): String {
    val bigDecimalAmount = number.toBigDecimal().stripTrailingZeros()
    val maxFractionDigits = bigDecimalAmount.scale().coerceAtLeast(minDecimals)
    val formatter = NumberFormat.getInstance(locale).apply {
        roundingMode = RoundingMode.HALF_EVEN
        maximumFractionDigits = maxFractionDigits
        minimumFractionDigits = minDecimals
        minimumIntegerDigits = 1
    }
    return formatter.format(bigDecimalAmount)
}

private fun StringResource.ByZatoshi.convertZatoshi(): String {
    val amount = this.zatoshi.convertZatoshiToZecString()
    return when (this.tickerLocation) {
        TickerLocation.BEFORE -> "ZEC $amount"
        TickerLocation.AFTER -> "$amount ZEC"
        TickerLocation.HIDDEN -> amount
    }
}

private fun StringResource.ByDynamicCurrencyNumber.convertDynamicCurrencyNumber(locale: Locale): String {
    val amount = convertNumberToString(amount, locale)
    return when (this.tickerLocation) {
        TickerLocation.BEFORE -> "$ticker$amount"
        TickerLocation.AFTER -> "$amount $ticker"
        TickerLocation.HIDDEN -> amount
    }
}

private fun StringResource.ByDynamicNumber.convertDynamicNumber(locale: Locale): String =
    convertNumberToString(number, locale)

private fun convertNumberToString(number: Number, locale: Locale): String {
    val bigDecimalAmount = number.toBigDecimal()
    val dynamicAmount = bigDecimalAmount.stripFractionsDynamically(2)
    val maxDecimals = if (bigDecimalAmount.scale() > 0) bigDecimalAmount.scale() else 0
    val formatter = NumberFormat.getInstance(locale).apply {
        roundingMode = RoundingMode.HALF_EVEN
        maximumFractionDigits = maxDecimals.coerceAtLeast(2)
        minimumFractionDigits = 2
        minimumIntegerDigits = 1
    }
    return formatter.format(dynamicAmount)
}

private fun Number.toBigDecimal() = when (this) {
    is BigDecimal -> this
    is Int -> BigDecimal(this)
    is Long -> BigDecimal(this)
    is Float -> BigDecimal(this.toDouble())
    is Double -> BigDecimal(this)
    is Short -> BigDecimal(this.toInt())
    else -> BigDecimal(this.toDouble())
}

private fun StringResource.ByDateTime.convertDateTime(): String {
    if (useFullFormat) {
        return DateFormat
            .getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
            ).format(
                Date.from(
                    zonedDateTime
                        .toInstant()
                        .toKotlinInstant()
                        .toJavaInstant()
                )
            )
    } else {
        val pattern = DateTimeFormatter.ofPattern("MMM dd")
        val start = zonedDateTime.format(pattern).orEmpty()
        val end =
            DateFormat
                .getTimeInstance(DateFormat.SHORT)
                .format(
                    Date.from(
                        zonedDateTime
                            .toInstant()
                            .toKotlinInstant()
                            .toJavaInstant()
                    )
                )

        return "$start $end"
    }
}

private fun StringResource.ByYearMonth.convertYearMonth(): String {
    val pattern = DateTimeFormatter.ofPattern("MMMM yyyy")
    return yearMonth.format(pattern).orEmpty()
}

private fun StringResource.ByAddress.convertAddress(): String =
    if (abbreviated && address.isNotBlank()) {
        "${address.take(ADDRESS_MAX_LENGTH_ABBREVIATED)}..."
    } else {
        address
    }

private fun StringResource.ByTransactionId.convertTransactionId(): String =
    if (abbreviated) {
        "${transactionId.take(TRANSACTION_MAX_PREFIX_SUFFIX_LENGHT)}...${
            transactionId.takeLast(TRANSACTION_MAX_PREFIX_SUFFIX_LENGHT)
        }"
    } else {
        transactionId
    }

private const val TRANSACTION_MAX_PREFIX_SUFFIX_LENGHT = 5

private const val ADDRESS_MAX_LENGTH_ABBREVIATED = 20

enum class TickerLocation { BEFORE, AFTER, HIDDEN }

private fun BigDecimal.stripFractionsDynamically(minDecimals: Int): BigDecimal {
    val threshold = BigDecimal(".5")
    val original = this.stripTrailingZeros()
    val scale = original.scale()

    if (scale <= minDecimals) return this

    var current = this

    for (i in 1..scale - minDecimals) {
        val next = original.setScale(original.scale() - i, RoundingMode.HALF_EVEN)

        val diff = BigDecimal("100")
            .minus(
                next.divide(original, MathContext.DECIMAL128)
                    .multiply(BigDecimal("100"), MathContext.DECIMAL128)
            )

        if (diff > threshold) return current else current = next
    }

    return current
}
