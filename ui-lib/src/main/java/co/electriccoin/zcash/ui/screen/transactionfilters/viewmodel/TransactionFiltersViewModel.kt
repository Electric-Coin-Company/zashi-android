package co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.transactionfilters.fixture.TransactionFiltersStateFixture
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFiltersState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class TransactionFiltersViewModel(
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val hideBottomSheetRequest = MutableSharedFlow<Unit>()

    private val bottomSheetHiddenResponse = MutableSharedFlow<Unit>()

    val state: StateFlow<TransactionFiltersState?> =
        flow<TransactionFiltersState?> {
            emit(TransactionFiltersStateFixture.new())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    private suspend fun hideBottomSheet() {
        hideBottomSheetRequest.emit(Unit)
        bottomSheetHiddenResponse.first()
    }

    private fun onBottomSheetHidden() =
        viewModelScope.launch {
            bottomSheetHiddenResponse.emit(Unit)
        }

    private fun onBack() =
        viewModelScope.launch {
            hideBottomSheet()
            navigationRouter.back()
        }
}