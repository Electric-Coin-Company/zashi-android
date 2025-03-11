package co.electriccoin.zcash.ui.design.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.Zatoshi
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.text.DateFormat
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@Immutable
sealed interface StringResource {
    @Immutable
    data class ByResource(
        @StringRes val resource: Int,
        val args: List<Any>
    ) : StringResource

    @JvmInline
    @Immutable
    value class ByString(
        val value: String
    ) : StringResource

    @Immutable
    data class ByZatoshi(
        val zatoshi: Zatoshi
    ) : StringResource

    @Immutable
    data class ByDateTime(
        val zonedDateTime: ZonedDateTime,
        val useFullFormat: Boolean
    ) : StringResource

    @Immutable
    data class ByYearMonth(
        val yearMonth: YearMonth
    ) : StringResource

    @Immutable
    data class ByTransactionId(
        val transactionId: String,
        val abbreviated: Boolean
    ) : StringResource

    @Immutable
    data class ByAddress(
        val address: String,
        val abbreviated: Boolean
    ) : StringResource
}

@Stable
fun stringRes(
    @StringRes resource: Int,
    vararg args: Any
): StringResource = StringResource.ByResource(resource, args.toList())

@Stable
fun stringRes(value: String): StringResource = StringResource.ByString(value)

@Stable
fun stringRes(zatoshi: Zatoshi): StringResource = StringResource.ByZatoshi(zatoshi)

@Stable
fun stringResByDateTime(
    zonedDateTime: ZonedDateTime,
    useFullFormat: Boolean
): StringResource =
    StringResource.ByDateTime(
        zonedDateTime = zonedDateTime,
        useFullFormat = useFullFormat
    )

@Stable
fun stringRes(yearMonth: YearMonth): StringResource = StringResource.ByYearMonth(yearMonth)

@Stable
fun stringResByAddress(
    value: String,
    abbreviated: Boolean
): StringResource =
    StringResource.ByAddress(
        value,
        abbreviated
    )

@Stable
fun stringResByTransactionId(
    value: String,
    abbreviated: Boolean
): StringResource = StringResource.ByTransactionId(value, abbreviated)

@Suppress("SpreadOperator")
@Stable
@Composable
fun StringResource.getValue(
    convertZatoshi: (Zatoshi) -> String = StringResourceDefaults::convertZatoshi,
    convertDateTime: (StringResource.ByDateTime) -> String = StringResourceDefaults::convertDateTime,
    convertYearMonth: (YearMonth) -> String = StringResourceDefaults::convertYearMonth,
    convertAddress: (StringResource.ByAddress) -> String = StringResourceDefaults::convertAddress,
    convertTransactionId: (StringResource.ByTransactionId) -> String = StringResourceDefaults::convertTransactionId
) = when (this) {
    is StringResource.ByResource -> {
        val context = LocalContext.current
        context.getString(resource, *args.normalize(context).toTypedArray())
    }
    is StringResource.ByString -> value
    is StringResource.ByZatoshi -> convertZatoshi(zatoshi)
    is StringResource.ByDateTime -> convertDateTime(this)
    is StringResource.ByYearMonth -> convertYearMonth(yearMonth)
    is StringResource.ByAddress -> convertAddress(this)
    is StringResource.ByTransactionId -> convertTransactionId(this)
}

@Suppress("SpreadOperator")
fun StringResource.getString(
    context: Context,
    convertZatoshi: (Zatoshi) -> String = StringResourceDefaults::convertZatoshi,
    convertDateTime: (StringResource.ByDateTime) -> String = StringResourceDefaults::convertDateTime,
    convertYearMonth: (YearMonth) -> String = StringResourceDefaults::convertYearMonth,
    convertAddress: (StringResource.ByAddress) -> String = StringResourceDefaults::convertAddress,
    convertTransactionId: (StringResource.ByTransactionId) -> String = StringResourceDefaults::convertTransactionId
) = when (this) {
    is StringResource.ByResource -> context.getString(resource, *args.normalize(context).toTypedArray())
    is StringResource.ByString -> value
    is StringResource.ByZatoshi -> convertZatoshi(zatoshi)
    is StringResource.ByDateTime -> convertDateTime(this)
    is StringResource.ByYearMonth -> convertYearMonth(yearMonth)
    is StringResource.ByAddress -> convertAddress(this)
    is StringResource.ByTransactionId -> convertTransactionId(this)
}

private fun List<Any>.normalize(context: Context): List<Any> =
    this.map {
        when (it) {
            is StringResource -> it.getString(context)
            else -> it
        }
    }

object StringResourceDefaults {
    fun convertZatoshi(zatoshi: Zatoshi) = zatoshi.convertZatoshiToZecString()

    fun convertDateTime(res: StringResource.ByDateTime): String {
        if (res.useFullFormat) {
            return DateFormat
                .getDateTimeInstance(
                    DateFormat.MEDIUM,
                    DateFormat.SHORT,
                ).format(
                    Date.from(
                        res.zonedDateTime
                            .toInstant()
                            .toKotlinInstant()
                            .toJavaInstant()
                    )
                )
        } else {
            val pattern = DateTimeFormatter.ofPattern("MMM dd")
            val start = res.zonedDateTime.format(pattern).orEmpty()
            val end =
                DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(
                        Date.from(
                            res.zonedDateTime
                                .toInstant()
                                .toKotlinInstant()
                                .toJavaInstant()
                        )
                    )

            return "$start $end"
        }
    }

    fun convertYearMonth(yearMonth: YearMonth): String {
        val pattern = DateTimeFormatter.ofPattern("MMMM yyyy")
        return yearMonth.format(pattern).orEmpty()
    }

    fun convertAddress(res: StringResource.ByAddress): String =
        if (res.abbreviated && res.address.isNotBlank()) {
            "${res.address.take(ADDRESS_MAX_LENGTH_ABBREVIATED)}..."
        } else {
            res.address
        }

    fun convertTransactionId(res: StringResource.ByTransactionId): String =
        if (res.abbreviated) {
            "${res.transactionId.take(TRANSACTION_MAX_PREFIX_SUFFIX_LENGHT)}...${res.transactionId.takeLast(
                TRANSACTION_MAX_PREFIX_SUFFIX_LENGHT
            )}"
        } else {
            res.transactionId
        }
}

private const val TRANSACTION_MAX_PREFIX_SUFFIX_LENGHT = 5

private const val ADDRESS_MAX_LENGTH_ABBREVIATED = 20
