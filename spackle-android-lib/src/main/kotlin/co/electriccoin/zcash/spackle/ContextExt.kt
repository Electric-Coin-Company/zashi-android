@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.spackle

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.getExternalFilesDirSuspend(type: String?) =
    withContext(Dispatchers.IO) {
        getExternalFilesDir(type)
    }
