package co.electriccoin.zcash.crash.android.internal.local

import android.content.Context
import android.os.Looper
import androidx.annotation.MainThread
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.crash.android.R
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

internal class AndroidUncaughtExceptionHandler(
    context: Context,
    private val defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler
) : Thread.UncaughtExceptionHandler {

    private val applicationContext = context.applicationContext

    override fun uncaughtException(t: Thread, e: Throwable) {
        val reportableException = ReportableException.new(applicationContext, e, true)

        val isUseSecondaryProcess = applicationContext.resources
            .getBoolean(R.bool.co_electriccoin_zcash_crash_is_use_secondary_process)

        if (isUseSecondaryProcess) {
            applicationContext.sendBroadcast(ExceptionReceiver.newIntent(applicationContext, reportableException))
        } else {
            runBlocking { AndroidExceptionReporter.reportException(applicationContext, reportableException) }
        }

        defaultUncaughtExceptionHandler.uncaughtException(t, e)
    }

    companion object {

        private val isInitialized = AtomicBoolean(false)

        /**
         * Call to register writing uncaught exceptions to external storage.
         */
        @MainThread
        internal fun register(context: Context) {
            check(Looper.myLooper() == Looper.getMainLooper()) { "Must be called from the main thread" }
            check(!isInitialized.getAndSet(true)) { "Uncaught exception handler can only be set once" }

            Thread.getDefaultUncaughtExceptionHandler()?.let { previous ->
                Thread.setDefaultUncaughtExceptionHandler(AndroidUncaughtExceptionHandler(context, previous))
            }
        }
    }
}
