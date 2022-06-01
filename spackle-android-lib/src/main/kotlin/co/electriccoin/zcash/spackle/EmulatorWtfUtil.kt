package co.electriccoin.zcash.spackle

import android.content.Context
import android.provider.Settings

/*
 * This is not under a test module, because there are some code paths that we might want to alter
 * during Emulator WTF tests.
 */
object EmulatorWtfUtil {
    private const val EMULATOR_WTF_SETTING = "emulator.wtf" // $NON-NLS
    private const val SETTING_TRUE = "true" // $NON-NLS

    private val isEmulatorWtfCached = LazyWithArgument<Context, Boolean> {
        isEmulatorWtfImpl(it)
    }

    /**
     * @return True if the environment is emulator.wtf
     */
    fun isEmulatorWtf(context: Context) = isEmulatorWtfCached.getInstance(context)

    private fun isEmulatorWtfImpl(context: Context): Boolean {
        // Tested with the benchmark library, this is very fast.  There shouldn't be a need to make
        // this a suspend function.  That said, we'll still cache the result as a just-in-case
        // since IPC may be involved.
        return runCatching {
            SETTING_TRUE == Settings.System.getString(context.contentResolver, EMULATOR_WTF_SETTING)
        }.recover {
            // Fail-safe in case an error occurs
            // 99.9% of the time, it won't be Emulator.wtf
            false
        }.getOrThrow()
    }
}
