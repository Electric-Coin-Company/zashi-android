package co.electriccoin.zcash.spackle

import android.content.Context
import android.provider.Settings

/*
 * This is not under a test module, because there are some code paths that we might want to alter
 * during Google Play Prelaunch reports.
 */
object FirebaseTestLabUtil {
    private const val FIREBASE_TEST_LAB_SETTING = "firebase.test.lab" // $NON-NLS
    private const val SETTING_TRUE = "true" // $NON-NLS

    private val isFirebaseTestLabCached = LazyWithArgument<Context, Boolean> {
        isFirebaseTestLabImpl(it)
    }

    /**
     * @return True if the environment is Firebase Test Lab.
     */
    fun isFirebaseTestLab(context: Context) = isFirebaseTestLabCached.getInstance(context)

    private fun isFirebaseTestLabImpl(context: Context): Boolean {
        /*
         * Per the documentation at https://firebase.google.com/docs/test-lab/android-studio
         */
        // Tested with the benchmark library, this is very fast.  There shouldn't be a need to make
        // this a suspend function.  That said, we'll still cache the result as a just-in-case
        // since IPC may be involved.
        return runCatching {
            SETTING_TRUE == Settings.System.getString(context.contentResolver, FIREBASE_TEST_LAB_SETTING)
        }.recover {
            // Fail-safe in case an error occurs
            // 99.9% of the time, it won't be Firebase Test Lab
            false
        }.getOrThrow()
    }
}
