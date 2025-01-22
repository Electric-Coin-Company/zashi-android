package co.electriccoin.zcash.ui.design.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Immutable
sealed interface StringResource {
    @Immutable
    data class ByResource(
        @StringRes val resource: Int,
        val args: List<Any>
    ) : StringResource

    @JvmInline
    @Immutable
    value class ByString(val value: String) : StringResource

    @Immutable
    data class ByZatoshi(val zatoshi: Zatoshi): StringResource

    @Immutable
    data class ByDateTime(val zonedDateTime: ZonedDateTime): StringResource
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

fun stringRes(zonedDateTime: ZonedDateTime): StringResource = StringResource.ByDateTime(zonedDateTime)

@Suppress("SpreadOperator")
@Stable
@Composable
fun StringResource.getValue(
    convertZatoshi: (Zatoshi) -> String = StringResourceDefaults::convertZatoshi,
    convertDateTime: (ZonedDateTime) -> String = StringResourceDefaults::convertDateTime
) = when (this) {
        is StringResource.ByResource -> {
            val context = LocalContext.current
            context.getString(resource, *args.normalize(context).toTypedArray())
        }
        is StringResource.ByString -> value
        is StringResource.ByZatoshi -> convertZatoshi(zatoshi)
        is StringResource.ByDateTime -> convertDateTime(zonedDateTime)
    }

@Suppress("SpreadOperator")
fun StringResource.getString(
    context: Context,
    convertZatoshi: (Zatoshi) -> String = StringResourceDefaults::convertZatoshi,
    convertDateTime: (ZonedDateTime) -> String = StringResourceDefaults::convertDateTime
) =
    when (this) {
        is StringResource.ByResource -> context.getString(resource, *args.normalize(context).toTypedArray())
        is StringResource.ByString -> value
        is StringResource.ByZatoshi -> convertZatoshi(zatoshi)
        is StringResource.ByDateTime -> convertDateTime(zonedDateTime)
    }

private fun List<Any>.normalize(context: Context): List<Any> =
    this.map {
        when (it) {
            is StringResource -> it.getString(context)
            else -> it
        }
    }

object StringResourceDefaults {
    fun convertZatoshi(zatoshi: Zatoshi) = zatoshi.convertZatoshiToZecString(
        maxDecimals = ZATOSHI_MAX_DECIMALS,
        minDecimals = ZATOSHI_MAX_DECIMALS
    )

    fun convertDateTime(zonedDateTime: ZonedDateTime): String {
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return zonedDateTime.format(pattern).orEmpty()
    }

}

private const val ZATOSHI_MAX_DECIMALS = 3
