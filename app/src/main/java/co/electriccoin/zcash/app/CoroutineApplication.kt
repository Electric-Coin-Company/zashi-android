package co.electriccoin.zcash.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class CoroutineApplication : Application() {
    protected lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onTerminate() {
        applicationScope.coroutineContext.cancel()
        super.onTerminate()
    }
}
