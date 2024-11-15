package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SensitiveSettingsVisibleUseCase(
    appUpdateChecker: AppUpdateChecker,
    context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val flow =
        appUpdateChecker.newCheckForUpdateAvailabilityFlow(context)
            .map { it.isForce.not() }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = true
            )

    operator fun invoke() = flow
}
