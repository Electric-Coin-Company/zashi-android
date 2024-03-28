package co.electriccoin.zcash.ui.screen.account.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.screen.account.model.HistoryItemExpandableState
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TransactionHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val transactions: MutableStateFlow<ImmutableList<TransactionUi>> = MutableStateFlow(persistentListOf())

    val transactionUiState: StateFlow<TransactionUiState> =
        transactions.map {
            if (it.isEmpty()) {
                TransactionUiState.Syncing
            } else {
                TransactionUiState.Prepared(it)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            TransactionUiState.Loading
        )

    fun processTransactionState(dataState: TransactionHistorySyncState) {
        transactions.value =
            when (dataState) {
                TransactionHistorySyncState.Loading -> persistentListOf()
                is TransactionHistorySyncState.Prepared -> {
                    dataState.transactions.map { data ->
                        TransactionUi.new(
                            data = data,
                            expandableState =
                                transactions.value.find {
                                    data.overview.rawId == it.overview.rawId
                                }?.expandableState ?: HistoryItemExpandableState.COLLAPSED
                        )
                    }.toPersistentList()
                }
            }
    }

    fun updateTransactionItemState(
        txId: FirstClassByteArray,
        newState: HistoryItemExpandableState
    ) {
        transactions.value =
            transactions.value.map { item ->
                if (item.overview.rawId == txId) {
                    item.copy(expandableState = newState)
                } else {
                    item
                }
            }.toPersistentList()
    }
}
