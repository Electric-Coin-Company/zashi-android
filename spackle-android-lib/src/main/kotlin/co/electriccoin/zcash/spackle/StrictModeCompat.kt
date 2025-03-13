package co.electriccoin.zcash.spackle

import android.annotation.SuppressLint
import android.os.StrictMode

object StrictModeCompat {
    fun enableStrictMode(isCrashOnViolation: Boolean) {
        configureStrictMode(isCrashOnViolation)
    }

    @SuppressLint("NewApi")
    private fun configureStrictMode(isCrashOnViolation: Boolean) {
        StrictMode.enableDefaults()

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy
                .Builder()
                .apply {
                    detectAll()
                    if (isCrashOnViolation) {
                        penaltyDeath()
                    } else {
                        penaltyLog()
                    }
                }.build()
        )

        // Don't enable missing network tags, because those are noisy.
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy
                .Builder()
                .apply {
                    if (AndroidApiVersion.isAtLeastS) {
                        detectUnsafeIntentLaunch()
                    }
                    detectActivityLeaks()
                    detectCleartextNetwork()
                    detectContentUriWithoutPermission()
                    detectFileUriExposure()
                    detectLeakedClosableObjects()
                    detectLeakedRegistrationObjects()
                    detectLeakedSqlLiteObjects()
                    if (AndroidApiVersion.isAtLeastP) {
                        // Disable because this is mostly flagging Android X and Play Services
                        // builder.detectNonSdkApiUsage();
                    }

                    if (isCrashOnViolation) {
                        penaltyDeath()
                    } else {
                        penaltyLog()
                    }
                }.build()
        )
    }
}
