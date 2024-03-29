package co.electriccoin.zcash.ui.screen.account.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionOverview
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.model.TrxItemState
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
import kotlinx.coroutines.flow.toList

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
                        val existingTransaction =
                            transactions.value.find {
                                data.overview.rawId == it.overview.rawId
                            }
                        TransactionUi.new(
                            data = data,
                            expandableState = existingTransaction?.expandableState ?: TrxItemState.COLLAPSED,
                            messages = existingTransaction?.messages,
                        )
                    }.toPersistentList()
                }
            }
    }

    private fun updateTransaction(newTransaction: TransactionUi) {
        transactions.value =
            transactions.value.map { item ->
                if (item.overview.rawId == newTransaction.overview.rawId) {
                    newTransaction
                } else {
                    item
                }
            }.toPersistentList()
    }

    suspend fun updateTransactionItemState(
        synchronizer: Synchronizer,
        txId: FirstClassByteArray,
        newState: TrxItemState
    ) {
        val updated =
            transactions.value
                .find { it.overview.rawId == txId }
                ?.copy(expandableState = newState)

        if (updated != null) {
            var updatedWithMessages = updated
            // Expanding the item on the first time -> load messages
            if (newState == TrxItemState.EXPANDED && updated.messages == null) {
                val messages = loadMessageForTransaction(synchronizer, updated.overview)
                updatedWithMessages = updated.copy(messages = messages)
            }
            updateTransaction(updatedWithMessages)
        } else {
            Twig.warn { "Transaction not found" }
        }
    }

    private suspend fun loadMessageForTransaction(
        synchronizer: Synchronizer,
        overview: TransactionOverview
    ): List<String> =
        synchronizer.getMemos(overview).toList().also {
            Twig.info { "Transaction messages count: ${it.size}" }
        }
}
