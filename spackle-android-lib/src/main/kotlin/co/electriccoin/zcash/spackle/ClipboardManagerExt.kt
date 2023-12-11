@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.spackle

import android.content.ClipData
import android.content.ClipboardManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ClipboardManager.setPrimaryClipSuspend(data: ClipData) =
    withContext(Dispatchers.IO) {
        setPrimaryClip(data)
    }
