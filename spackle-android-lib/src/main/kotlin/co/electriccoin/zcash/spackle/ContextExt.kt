@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.spackle

import android.content.Context
import co.electriccoin.zcash.spackle.io.mkdirsSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// TODO [#1182]: Cover ContextExt with tests
// TODO [#1182]: https://github.com/Electric-Coin-Company/zashi-android/issues/1182

suspend fun Context.getExternalFilesDirSuspend(type: String?) =
    withContext(Dispatchers.IO) {
        getExternalFilesDir(type)
    }

suspend fun Context.getInternalCacheDirSuspend(subDirectory: String?): File =
    withContext(Dispatchers.IO) {
        (subDirectory?.let { File(cacheDir, subDirectory) } ?: cacheDir).apply { mkdirsSuspend() }
    }
