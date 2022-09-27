package co.electriccoin.zcash.ui.screen.warning.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.global.StorageChecker
import co.electriccoin.zcash.ui.common.toStateFlow
import kotlinx.coroutines.flow.flow

class StorageCheckViewModel : ViewModel() {
    @Suppress("MagicNumber")
    val requiredStorageSpaceGigabytes: String =
        (StorageChecker.REQUIRED_FREE_SPACE_MEGABYTES / 1000).toString()

    val isEnoughSpace = flow { emit(StorageChecker.isEnoughSpace()) }.toStateFlow(viewModelScope)

    val spaceRequiredToContinue =
        flow { emit(StorageChecker.spaceRequiredToContinue().toString()) }.toStateFlow(viewModelScope)
}
