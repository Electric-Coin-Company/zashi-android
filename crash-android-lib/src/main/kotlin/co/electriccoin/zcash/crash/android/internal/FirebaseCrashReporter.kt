package co.electriccoin.zcash.crash.android.internal

import android.content.Context
import androidx.annotation.AnyThread
import co.electriccoin.zcash.crash.android.R
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.spackle.LazyWithArgument
import co.electriccoin.zcash.spackle.Twig
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations

/**
 * Registers an exception handler with Firebase Crashlytics.
 */
internal class FirebaseCrashReporter(
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val firebaseInstallations: FirebaseInstallations
) : CrashReporter {

    @AnyThread
    override fun reportCaughtException(exception: Throwable) {
        firebaseCrashlytics.recordException(exception)
    }

    override fun enable() {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
    }

    override fun disableAndDelete() {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(false)
        firebaseCrashlytics.deleteUnsentReports()
        firebaseInstallations.delete()
    }

    companion object {
        private val lazyWithArgument = LazyWithArgument<Context, CrashReporter?> {
            if (it.resources.getBoolean(R.bool.co_electriccoin_zcash_crash_is_firebase_enabled)) {
                FirebaseApp.initializeApp(it)
                val firebaseInstallations = FirebaseInstallations.getInstance()
                val firebaseCrashlytics = FirebaseCrashlytics.getInstance().apply {
                    setCustomKey(
                        CrashlyticsUserProperties.IS_TEST,
                        EmulatorWtfUtil.isEmulatorWtf(it) || FirebaseTestLabUtil.isFirebaseTestLab(it)
                    )
                }

                FirebaseCrashReporter(firebaseCrashlytics, firebaseInstallations)
            } else {
                Twig.warn { "Unable to initialize Crashlytics. Configure API keys in the app module" }
                null
            }
        }

        fun getInstance(context: Context): CrashReporter? {
            return lazyWithArgument.getInstance(context)
        }
    }
}

internal object CrashlyticsUserProperties {
    /**
     * Flags a crash as occurring in a test environment.  Set automatically to detect Firebase Test Lab and emulator.wtf
     */
    const val IS_TEST = "is_test" // $NON-NLS
}
