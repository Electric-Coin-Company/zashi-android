package cash.z.ecc.app

import android.app.Application
import cash.z.ecc.BuildConfig

@Suppress("unused")
class AppImpl : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictModeHelper.enableStrictMode()
        }
    }
}
