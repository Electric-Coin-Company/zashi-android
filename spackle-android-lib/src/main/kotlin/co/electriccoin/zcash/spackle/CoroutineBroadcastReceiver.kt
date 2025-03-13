package co.electriccoin.zcash.spackle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @param broadcastReceiverScope Scope for performing asynchronous work in the broadcast receiver.
 * It is not recommended to cancel this scope.
 */
abstract class CoroutineBroadcastReceiver(
    private val broadcastReceiverScope: CoroutineScope
) : BroadcastReceiver() {
    final override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val pendingResult = goAsync()

        broadcastReceiverScope.launch {
            onReceiveSuspend(context, intent)

            // Race condition here: if the broadcastReceiverScope is canceled before this
            // completes, then the BroadcastReceiver will trigger an Application Not Responding
            // because the PendingResult was leaked.
            pendingResult.finish()
        }
    }

    /**
     * Override to perform work asynchronously.  Note that this method must be quick to avoid
     * the Android timeout for broadcast receivers.  This method is suitable for brief disk IO but
     * not suitable for network calls.
     */
    abstract suspend fun onReceiveSuspend(
        context: Context,
        intent: Intent
    )
}
