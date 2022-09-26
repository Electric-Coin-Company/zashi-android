package co.electriccoin.zcash.global

import android.annotation.SuppressLint
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StorageChecker {
    private const val REQUIRED_FREE_SPACE = 1000

    suspend fun isEnoughSpace() = checkAvailableStorage() > REQUIRED_FREE_SPACE

    @SuppressLint("UsableSpace")
    private suspend fun checkAvailableStorage() = withContext(Dispatchers.IO) {
        return@withContext Environment.getDataDirectory().usableSpace / (1024 * 1024)
    }
}



