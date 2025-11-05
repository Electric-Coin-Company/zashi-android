package co.electriccoin.zcash.ui.common.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

interface ApplicationStateRepository {
    fun init()
}

class ApplicationStateRepositoryImpl(
    private val appLifecycle: Lifecycle,
    private val applicationStateProvider: ApplicationStateProvider,
    private val synchronizerProvider: SynchronizerProvider,
) : ApplicationStateRepository {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun init() {
        observeApplicationLifecycle()
        notifySynchronizerOnApplicationState()
    }

    private fun observeApplicationLifecycle() {
        appLifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                applicationStateProvider.onApplicationLifecycleChanged(event)
            }
        )
    }

    private fun notifySynchronizerOnApplicationState() {
        scope.launch {
            combine(
                synchronizerProvider.synchronizer,
                applicationStateProvider.isInForeground,
            ) { synchronizer, isInForeground -> synchronizer to isInForeground }
                .collect { (synchronizer, isInForeground) ->
                    if (isInForeground) {
                        synchronizer?.onForeground()
                    } else {
                        synchronizer?.onBackground()
                    }
                }
        }
    }
}
