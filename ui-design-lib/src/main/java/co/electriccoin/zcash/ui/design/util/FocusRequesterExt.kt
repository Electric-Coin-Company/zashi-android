package co.electriccoin.zcash.ui.design.util

import androidx.compose.ui.focus.FocusRequester

/**
 * @see [FocusRequester.requestFocus]
 *
 * @return true if the focus was successfully requested, false if the focus request was canceled or null if request
 * focus failed
 */
fun FocusRequester.tryRequestFocus(): Boolean? =
    try {
        requestFocus()
    } catch (_: IllegalStateException) {
        null
    }
