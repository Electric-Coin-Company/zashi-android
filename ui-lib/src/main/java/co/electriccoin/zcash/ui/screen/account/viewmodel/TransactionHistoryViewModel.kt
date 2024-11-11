package co.electriccoin.zcash.ui.screen.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.account.model.TrxItemState
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val observeAddressBookContacts: ObserveAddressBookContactsUseCase
) : ViewModel() {
    private val state: MutableStateFlow<State> = MutableStateFlow(State.LOADING)

    private val transactions: MutableStateFlow<ImmutableList<TransactionUi>> = MutableStateFlow(persistentListOf())

    private var transactionSyncJob: Job? = null

    val transactionUiState: StateFlow<TransactionUiState> =
        state.combine(transactions) { state: State, transactions: ImmutableList<TransactionUi> ->
            when (state) {
                State.LOADING -> TransactionUiState.Loading
                State.SYNCING -> TransactionUiState.Syncing(transactions)
                State.SYNCING_EMPTY -> TransactionUiState.SyncingEmpty
                State.DONE -> TransactionUiState.Done(transactions)
                State.DONE_EMPTY -> TransactionUiState.DoneEmpty
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            TransactionUiState.Loading
        )

    fun processTransactionState(dataState: TransactionHistorySyncState) {
        transactionSyncJob?.cancel()
        transactionSyncJob =
            viewModelScope.launch {
                when (dataState) {
                    TransactionHistorySyncState.Loading -> {
                        state.value = State.LOADING
                        transactions.value = persistentListOf()
                    }

                    is TransactionHistorySyncState.Syncing -> {
                        if (dataState.transactions.isEmpty()) {
                            state.value = State.SYNCING_EMPTY
                        } else {
                            state.value = State.SYNCING

                            observeAddressBookContacts()
                                .map { contacts ->
                                    dataState.transactions
                                        .map { data ->
                                            val contact =
                                                contacts?.find { contact ->
                                                    contact.address ==
                                                        (data.recipient as? TransactionRecipient.Address)
                                                            ?.addressValue
                                                }
                                            getOrUpdateTransactionItem(data, contact)
                                        }
                                        .toPersistentList()
                                }
                                .onEach { new -> transactions.update { new } }
                                .launchIn(this)
                        }
                    }

                    is TransactionHistorySyncState.Done -> {
                        if (dataState.transactions.isEmpty()) {
                            state.value = State.DONE_EMPTY
                        } else {
                            state.value = State.DONE

                            observeAddressBookContacts()
                                .map { contacts ->
                                    dataState.transactions
                                        .map { data ->
                                            val contact =
                                                contacts?.find { contact ->
                                                    contact.address ==
                                                        (data.recipient as? TransactionRecipient.Address)
                                                            ?.addressValue
                                                }
                                            getOrUpdateTransactionItem(data, contact)
                                        }
                                        .toPersistentList()
                                }
                                .onEach { new -> transactions.update { new } }
                                .launchIn(this)
                        }
                    }
                }
            }
    }

    private fun getOrUpdateTransactionItem(
        data: TransactionOverviewExt,
        addressBookContact: AddressBookContact?
    ): TransactionUi {
        val existingTransaction =
            transactions.value.find {
                data.overview.rawId == it.overview.rawId
            }
        return TransactionUi.new(
            data = data,
            expandableState = existingTransaction?.expandableState ?: TrxItemState.COLLAPSED,
            messages = existingTransaction?.messages,
            addressBookContact = addressBookContact
        )
    }

    private fun updateTransactionInList(newTransaction: TransactionUi) {
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
            updateTransactionInList(updatedWithMessages)
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

private enum class State {
    LOADING,
    SYNCING,
    SYNCING_EMPTY,
    DONE,
    DONE_EMPTY,
}
