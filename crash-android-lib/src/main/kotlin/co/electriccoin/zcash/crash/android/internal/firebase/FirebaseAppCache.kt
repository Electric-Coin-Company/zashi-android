package co.electriccoin.zcash.crash.android.internal.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

object FirebaseAppCache {
    private val mutex = Mutex()

    @Volatile
    private var cachedFirebaseApp: FirebaseAppContainer? = null

    fun peekFirebaseApp(): FirebaseApp? = cachedFirebaseApp?.firebaseApp

    suspend fun getFirebaseApp(context: Context): FirebaseApp? {
        mutex.withLock {
            peekFirebaseApp()?.let {
                return it
            }

            val firebaseAppContainer = getFirebaseAppContainer(context)

            cachedFirebaseApp = firebaseAppContainer
        }

        return peekFirebaseApp()
    }
}

private suspend fun getFirebaseAppContainer(context: Context): FirebaseAppContainer = withContext(Dispatchers.IO) {
    val firebaseApp = FirebaseApp.initializeApp(context)
    FirebaseAppContainer(firebaseApp)
}

private class FirebaseAppContainer(val firebaseApp: FirebaseApp?)
