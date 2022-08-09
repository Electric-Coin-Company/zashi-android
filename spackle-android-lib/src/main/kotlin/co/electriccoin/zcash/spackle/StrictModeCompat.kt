package co.electriccoin.zcash.spackle

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.StrictMode

object StrictModeCompat {

    fun enableStrictMode(isCrashOnViolation: Boolean) {
        configureStrictMode(isCrashOnViolation)

        // Workaround for Android bug
        // https://issuetracker.google.com/issues/36951662
        // Not needed if target O_MR1 and running on O_MR1
        // Don't really need to check target, because of Google Play enforcement on targetSdkVersion for app updates
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                configureStrictMode(isCrashOnViolation)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun configureStrictMode(isCrashOnViolation: Boolean) {
        StrictMode.enableDefaults()

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().apply {
                detectAll()
                if (isCrashOnViolation) {
                    penaltyDeath()
                } else {
                    penaltyLog()
                }
            }.build()
        )

        // Don't enable missing network tags, because those are noisy.
        if (AndroidApiVersion.isAtLeastO) {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().apply {
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
        } else {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().apply {
                    detectAll()
                    if (isCrashOnViolation) {
                        penaltyDeath()
                    } else {
                        penaltyLog()
                    }
                }.build()
            )
        }
    }
}
