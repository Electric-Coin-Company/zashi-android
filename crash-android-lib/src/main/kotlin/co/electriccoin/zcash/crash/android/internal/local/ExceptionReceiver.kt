package co.electriccoin.zcash.crash.android.internal.local

import android.content.Context
import android.content.Intent
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.spackle.CoroutineBroadcastReceiver
import kotlinx.coroutines.GlobalScope

@OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
class ExceptionReceiver : CoroutineBroadcastReceiver(GlobalScope) {
    override suspend fun onReceiveSuspend(
        context: Context,
        intent: Intent
    ) {
        val reportableException =
            intent.extras?.let { ReportableException.fromBundle(it) }
                ?: return

        AndroidExceptionReporter.reportException(context, reportableException)
    }

    companion object {
        /**
         * @return Explicit intent to broadcast to log the exception.
         */
        fun newIntent(
            context: Context,
            reportableException: ReportableException
        ) = Intent(context, ExceptionReceiver::class.java).apply {
            // Use Intent.FLAG_RECEIVER_FOREGROUND to reduce likelihood that Android throttles
            // the Intents, since the foreground receiver queue is usually significantly less loaded
            // than the default background receiver queue.  One tradeoff is that FOREGROUND Intents
            // have less time (5 seconds) to do their work.
            flags = Intent.FLAG_RECEIVER_FOREGROUND
            putExtras(reportableException.toBundle())
        }
    }
}
