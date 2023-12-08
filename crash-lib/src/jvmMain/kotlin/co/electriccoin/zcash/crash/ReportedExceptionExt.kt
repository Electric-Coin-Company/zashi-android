@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.crash

import kotlinx.datetime.Instant
import java.io.File

fun ReportedException.Companion.new(file: File): ReportedException? {
    // Exclude temp files
    if (file.extension == ExceptionPath.TYPE) {
        val name: String = file.nameWithoutExtension
        val splitName = name.split(ExceptionPath.SEPARATOR)

        val epochSeconds = splitName.firstOrNull()?.toLongOrNull()
        val classNameString = splitName.getOrNull(2)
        val isUncaught = splitName.lastOrNull()?.toBoolean()

        if (null != epochSeconds && null != classNameString && null != isUncaught) {
            return ReportedException(
                filePath = file.path,
                exceptionClassName = classNameString,
                isUncaught = isUncaught,
                time = Instant.fromEpochSeconds(epochSeconds)
            )
        }
    }

    return null
}
