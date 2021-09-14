package cash.z.ecc.android.app

import android.app.Application
import cash.z.ecc.android.sdk.demoapp.BuildConfig
import cash.z.ecc.android.sdk.ext.TroubleshootingTwig
import cash.z.ecc.android.sdk.ext.Twig

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictModeHelper.enableStrictMode()
        }

        Twig.plant(TroubleshootingTwig())
    }
}
