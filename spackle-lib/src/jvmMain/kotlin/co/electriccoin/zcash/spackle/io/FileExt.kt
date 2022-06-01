@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.spackle.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

suspend fun File.existsSuspend() = withContext(Dispatchers.IO) {
    exists()
}

suspend fun File.mkdirsSuspend() = withContext(Dispatchers.IO) {
    mkdirs()
}

suspend fun File.isDirectorySuspend() = withContext(Dispatchers.IO) {
    isDirectory
}

suspend fun File.isFileSuspend() = withContext(Dispatchers.IO) {
    isFile
}

suspend fun File.canWriteSuspend() = withContext(Dispatchers.IO) {
    canWrite()
}

suspend fun File.deleteSuspend() = withContext(Dispatchers.IO) {
    delete()
}

suspend fun File.renameToSuspend(destination: File) = withContext(Dispatchers.IO) {
    renameTo(destination)
}

suspend fun File.listFilesSuspend() = withContext(Dispatchers.IO) {
    listFiles()
}

/**
 * Given an ultimate output file destination, this generates a temporary file that [action] can write to.  After action
 * is complete, the temp file is renamed to the expected output destination.  Depending on the underlying filesystem,
 * this should effectively ensure that the file is perceived as being written atomically.
 *
 * @receiver Ultimate file that we desire to write to.  Must be a file and not a directory.
 * @param action Action to perform on the file, specifically this should be writing data.  This action should not
 * delete, rename, or do other operations in the filesystem.
 */
suspend fun File.writeAtomically(action: (suspend (File) -> Unit)) {
    val tempFile = withContext(Dispatchers.IO) {
        File(parentFile, name.newTempFileName()).also {
            it.deleteOnExit()
        }
    }

    var isWriteSuccessful = false

    try {
        action(tempFile)
        isWriteSuccessful = true
    } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
        tempFile.deleteSuspend()
        throw e
    } finally {
        if (isWriteSuccessful) {
            tempFile.moveTo(destination = this)
        }
    }
}

private suspend fun File.moveTo(destination: File) {
    val isRenameSuccessful = renameToSuspend(destination)

    if (!isRenameSuccessful) {
        if (existsSuspend()) {
            throw IOException("Couldn't move file $path to ${destination.path}")
        }

        // Otherwise no data was written, so there's no file to rename.
    }
}

// Note that adding uuid and .tmp could theoretically go past file name length limits on some filesystems
private fun String.newTempFileName() = "$this-${UUID.randomUUID()}.tmp"
