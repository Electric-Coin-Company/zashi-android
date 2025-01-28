package co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilter.BOOKMARKED
import co.electriccoin.zcash.ui.common.repository.TransactionFilter.MEMOS
import co.electriccoin.zcash.ui.common.repository.TransactionFilter.NOTES
import co.electriccoin.zcash.ui.common.repository.TransactionFilter.RECEIVED
import co.electriccoin.zcash.ui.common.repository.TransactionFilter.SENT
import co.electriccoin.zcash.ui.common.repository.TransactionFilter.UNREAD
import co.electriccoin.zcash.ui.common.usecase.ApplyTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFilterState
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFiltersState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class TransactionFiltersViewModel(
    private val navigationRouter: NavigationRouter,
    getTransactionFilters: GetTransactionFiltersUseCase,
    private val applyTransactionFilters: ApplyTransactionFiltersUseCase,
) : ViewModel() {
    val hideBottomSheetRequest = MutableSharedFlow<Unit>()

    private val bottomSheetHiddenResponse = MutableSharedFlow<Unit>()

    private val selectedFilters = MutableStateFlow(getTransactionFilters())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<TransactionFiltersState?> =
        selectedFilters.mapLatest { current ->
            TransactionFiltersState(
                filters =
                    TransactionFilter.entries.map {
                        when (it) {
                            SENT ->
                                TransactionFilterState(
                                    text = stringRes("Sent"),
                                    isSelected = current.contains(SENT),
                                    onClick = { onTransactionFilterClicked(it) }
                                )
                            RECEIVED ->
                                TransactionFilterState(
                                    text = stringRes("Received"),
                                    isSelected = current.contains(RECEIVED),
                                    onClick = { onTransactionFilterClicked(it) }
                                )
                            MEMOS ->
                                TransactionFilterState(
                                    text = stringRes("Memos"),
                                    isSelected = current.contains(MEMOS),
                                    onClick = { onTransactionFilterClicked(it) }
                                )
                            UNREAD ->
                                TransactionFilterState(
                                    text = stringRes("Unread"),
                                    isSelected = current.contains(UNREAD),
                                    onClick = { onTransactionFilterClicked(it) }
                                )
                            BOOKMARKED ->
                                TransactionFilterState(
                                    text = stringRes("Bookmarked"),
                                    isSelected = current.contains(BOOKMARKED),
                                    onClick = { onTransactionFilterClicked(it) }
                                )
                            NOTES ->
                                TransactionFilterState(
                                    text = stringRes("Notes"),
                                    isSelected = current.contains(NOTES),
                                    onClick = { onTransactionFilterClicked(it) }
                                )
                        }
                    },
                onBack = ::onBack,
                onBottomSheetHidden = ::onBottomSheetHidden,
                primaryButton =
                    ButtonState(
                        text = StringResource.ByString("Apply"),
                        onClick = ::onApplyTransactionFiltersClick,
                    ),
                secondaryButton =
                    ButtonState(
                        text = StringResource.ByString("Reset"),
                        onClick = ::onResetTransactionFiltersClick,
                    ),
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    private fun onTransactionFilterClicked(filter: TransactionFilter) {
        selectedFilters.update {
            if (it.contains(filter)) {
                it - filter
            } else {
                it + filter
            }
        }
    }

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

    private fun onApplyTransactionFiltersClick() =
        viewModelScope.launch {
            applyTransactionFilters(selectedFilters.value) { hideBottomSheet() }
        }

    private fun onResetTransactionFiltersClick() =
        viewModelScope.launch {
            selectedFilters.update { emptyList() }
        }
}
