package co.electriccoin.zcash.ui.design.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext

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
}

@Stable
fun stringRes(
    @StringRes resource: Int,
    vararg args: Any
): StringResource = StringResource.ByResource(resource, args.toList())

@Stable
fun stringRes(value: String): StringResource = StringResource.ByString(value)

@Suppress("SpreadOperator")
@Stable
@Composable
fun StringResource.getValue() =
    when (this) {
        is StringResource.ByResource -> {
            val context = LocalContext.current
            context.getString(resource, *args.normalize(context).toTypedArray())
        }

        is StringResource.ByString -> value
    }

@Suppress("SpreadOperator")
fun StringResource.getString(context: Context) =
    when (this) {
        is StringResource.ByResource -> context.getString(resource, *args.normalize(context).toTypedArray())
        is StringResource.ByString -> value
    }

private fun List<Any>.normalize(context: Context): List<Any> =
    this.map {
        when (it) {
            is StringResource -> it.getString(context)
            else -> it
        }
    }
