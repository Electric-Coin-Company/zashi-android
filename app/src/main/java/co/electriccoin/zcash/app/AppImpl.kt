package co.electriccoin.zcash.app

import android.app.Application
import co.electriccoin.zcash.BuildConfig

@Suppress("unused")
class AppImpl : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictModeHelper.enableStrictMode()
        }
    }
}
