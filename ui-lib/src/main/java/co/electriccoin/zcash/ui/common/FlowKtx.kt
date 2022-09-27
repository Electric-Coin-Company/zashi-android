@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

/**
 * Converts Flow to StateFlow with default sharing strategy and initial value set to null
 */
fun <T> Flow<T>.toStateFlow(
    coroutineScope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
    initialValue: T? = null
) = stateIn(
    coroutineScope,
    started,
    initialValue
)
