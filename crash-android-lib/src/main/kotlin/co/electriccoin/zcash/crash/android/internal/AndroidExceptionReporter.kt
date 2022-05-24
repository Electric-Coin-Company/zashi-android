package co.electriccoin.zcash.crash.android.internal

import android.content.Context
import android.media.MediaScannerConnection
import co.electriccoin.zcash.crash.ExceptionPath
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.crash.android.getExceptionPath
import co.electriccoin.zcash.crash.write

object AndroidExceptionReporter {
    internal suspend fun reportException(context: Context, reportableException: ReportableException) {
        val exceptionPath = ExceptionPath.getExceptionPath(context, reportableException)
            ?: return

        reportableException.write(exceptionPath)

        // Media Scan necessary for files to immediately show up as visible
        // Note: must break out of BroadcastReceiver context in order to start media
        // scanner service.
        MediaScannerConnection.scanFile(
            context.applicationContext,
            arrayOf(exceptionPath.absolutePath),
            null,
            null
        )
    }
}
