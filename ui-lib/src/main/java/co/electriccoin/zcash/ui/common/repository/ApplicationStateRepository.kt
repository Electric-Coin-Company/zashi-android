package co.electriccoin.zcash.ui.common.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface ApplicationStateRepository {
    fun init()
}

class ApplicationStateRepositoryImpl(
    private val appLifecycle: Lifecycle,
    private val applicationStateProvider: ApplicationStateProvider,
    private val synchronizerProvider: SynchronizerProvider,
    private val accountDataSource: AccountDataSource
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
                accountDataSource.selectedAccount.map { it?.sdkAccount?.accountUuid }.distinctUntilChanged()
            ) { synchronizer, isInForeground, account -> Triple(synchronizer, isInForeground, account) }
                .collect { (synchronizer, isInForeground, account) ->
                    if (isInForeground) {
                        synchronizer?.onForeground()
                        account?.let { synchronizer?.checkSingleUseTransparentAddress(it) }
                    } else {
                        synchronizer?.onBackground()
                    }
                }
        }
    }
}
