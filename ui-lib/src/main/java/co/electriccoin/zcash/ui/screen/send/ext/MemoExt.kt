package co.electriccoin.zcash.ui.screen.send.ext

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import cash.z.ecc.android.sdk.model.Memo
import co.electriccoin.zcash.ui.R

@Composable
@ReadOnlyComposable
internal fun Memo.valueOrEmptyChar(): String {
    LocalConfiguration.current
    return valueOrEmptyChar(LocalContext.current)
}

internal fun Memo.valueOrEmptyChar(context: Context): String {
    return value.ifEmpty { context.getString(R.string.empty_char) }
}
