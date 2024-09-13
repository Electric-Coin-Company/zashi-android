package co.electriccoin.zcash.spackle.process

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import co.electriccoin.zcash.spackle.AndroidApiVersion
import co.electriccoin.zcash.spackle.process.ProcessNameCompat.getProcessName

/**
 * Provides a reliable way of determining Android process name.  For highest reliability and performance,
 * [getProcessName] should only be called once the start of the Application.onCreate() callback has occurred
 * which will mean that the process name content provider has been initialized.
 *
 * Note that if you wish to add another process, consider adding an empty subclass of
 * [AbstractProcessNameContentProvider] in that process, as the ContentProvider has a more reliable
 * way to get process name on older Android versions.
 */
object ProcessNameCompat {
    // GuardedBy intrinsicLock
    private var processName: String? = null

    private val intrinsicLock = Any()

    fun getProcessName(context: Context): String {
        synchronized(intrinsicLock) {
            processName?.let {
                return it
            }

            val foundProcessName = searchForProcessName(context)
            if (null == foundProcessName) {
                // This should be exceedingly rare
                error("Unable to determine process name")
            } else {
                processName = foundProcessName
                return foundProcessName
            }
        }
    }

    /**
     * Not a public API; should only be called by [AbstractProcessNameContentProvider].
     */
    internal fun setProcessName(newProcessName: String) {
        processName = newProcessName
    }

    /**
     * @param context Application context.
     * @return Name of the current process.  May return null if a failure occurs, which is possible
     * due to some race conditions in Android.
     */
    private fun searchForProcessName(context: Context): String? {
        return if (AndroidApiVersion.isAtLeastTiramisu) {
            getProcessNameTPlus()
        } else if (AndroidApiVersion.isAtLeastP) {
            getProcessNamePPlus()
        } else {
            searchForProcessNameLegacy(context)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private fun getProcessNameTPlus() = Process.myProcessName()

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun getProcessNamePPlus() = Application.getProcessName()

    /**
     * @param context Application context.
     * @return Name of the current process.  May return null if a failure occurs, which is possible
     * due to some race conditions in older versions of Android.
     */
    @VisibleForTesting
    internal fun searchForProcessNameLegacy(context: Context): String? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        return activityManager.runningAppProcesses?.find { Process.myPid() == it.pid }?.processName
    }
}
