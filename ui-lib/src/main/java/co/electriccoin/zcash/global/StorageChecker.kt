package co.electriccoin.zcash.global

import android.annotation.SuppressLint
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StorageChecker {
    const val REQUIRED_FREE_SPACE_MEGABYTES: Int = 1000

    suspend fun isEnoughSpace() = checkAvailableStorage() > REQUIRED_FREE_SPACE_MEGABYTES

    @SuppressLint("UsableSpace")
    @Suppress("MagicNumber")
    suspend fun checkAvailableStorage(): Int = withContext(Dispatchers.IO) {
        return@withContext (Environment.getDataDirectory().usableSpace / (1024 * 1024)).toInt()
    }

    suspend fun spaceRequiredToContinue() = withContext(Dispatchers.IO) {
        return@withContext REQUIRED_FREE_SPACE_MEGABYTES - checkAvailableStorage()
    }
}
