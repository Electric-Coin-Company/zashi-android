package co.electriccoin.zcash.ui.screen.warning.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.global.StorageChecker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class StorageCheckViewModel : ViewModel() {
    @Suppress("MagicNumber")
    val requiredStorageSpaceGigabytes: Int =
        (StorageChecker.REQUIRED_FREE_SPACE_MEGABYTES / 1000)

    val isEnoughSpace =
        flow { emit(StorageChecker.isEnoughSpace()) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )

    val spaceRequiredToContinueMegabytes =
        flow { emit(StorageChecker.spaceRequiredToContinueMegabytes()) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )

    val spaceAvailableMegabytes =
        flow { emit(StorageChecker.checkAvailableStorageMegabytes()) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )
}
