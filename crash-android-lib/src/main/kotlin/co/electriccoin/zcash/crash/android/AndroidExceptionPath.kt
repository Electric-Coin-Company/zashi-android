package co.electriccoin.zcash.crash.android

import android.content.Context
import co.electriccoin.zcash.crash.ExceptionPath
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getExternalFilesDirSuspend
import java.io.File

@Suppress("ReturnCount")
suspend fun ExceptionPath.getExceptionDirectory(context: Context): File? {
    val exceptionDirectory = context.getExternalFilesDirSuspend(null)
        ?.let { File(File(it, ExceptionPath.LOG_DIRECTORY_NAME), ExceptionPath.EXCEPTION_DIRECTORY_NAME) }

    if (null == exceptionDirectory) {
        Twig.info { "Unable to get external storage directory; external storage may not be available" }
        return null
    }

    try {
        validateDir(exceptionDirectory)
    } catch (e: IllegalArgumentException) {
        Twig.info(e) { "Unable to get exception directory" }
        return null
    }

    return exceptionDirectory
}

suspend fun ExceptionPath.getExceptionPath(context: Context, exception: ReportableException): File? {
    val exceptionDirectory = getExceptionDirectory(context)
        ?: return null

    return File(exceptionDirectory, newExceptionFileName(exception))
}
