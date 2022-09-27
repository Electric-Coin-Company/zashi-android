package co.electriccoin.zcash.global

import android.annotation.SuppressLint
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StorageChecker {
    const val REQUIRED_FREE_SPACE_MEGABYTES = 1000

    suspend fun isEnoughSpace() = checkAvailableStorage() > REQUIRED_FREE_SPACE_MEGABYTES

    @SuppressLint("UsableSpace")
    @Suppress("MagicNumber")
    suspend fun checkAvailableStorage() = withContext(Dispatchers.IO) {
        return@withContext Environment.getDataDirectory().usableSpace / (1024 * 1024)
    }

    suspend fun spaceRequiredToContinue() = withContext(Dispatchers.IO) {
        return@withContext REQUIRED_FREE_SPACE_MEGABYTES - checkAvailableStorage()
    }
}
