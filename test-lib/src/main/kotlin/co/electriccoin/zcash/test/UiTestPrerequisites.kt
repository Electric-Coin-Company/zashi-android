package co.electriccoin.zcash.test

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Before

/**
 * Subclass this in view unit and integration tests.  This verifies that
 * prerequisites necessary for reliable UI tests are met, and it provides more useful error messages.
 */
// Originally hoped to put this into ZcashUiTestRunner, although it causes reporting of test results to fail
open class UiTestPrerequisites {
    @Before
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    fun verifyPrerequisites() {
        assertScreenIsOn()
        assertKeyguardIsUnlocked()
    }

    companion object {
        @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
        fun assertScreenIsOn() {
            if (!isScreenOn()) {
                throw AssertionError("Screen must be on for Android UI tests to run") // $NON-NLS
            }
        }

        @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
        private fun isScreenOn(): Boolean {
            val powerService = ApplicationProvider.getApplicationContext<Context>()
                .getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerService.isInteractive
        }

        fun assertKeyguardIsUnlocked() {
            if (isKeyguardLocked()) {
                throw AssertionError("Device must be unlocked on for Android UI tests to run") // $NON-NLS
            }
        }

        fun isKeyguardLocked(): Boolean {
            val keyguardService = (
                ApplicationProvider.getApplicationContext<Context>()
                    .getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                )

            return keyguardService.isKeyguardLocked
        }
    }
}
