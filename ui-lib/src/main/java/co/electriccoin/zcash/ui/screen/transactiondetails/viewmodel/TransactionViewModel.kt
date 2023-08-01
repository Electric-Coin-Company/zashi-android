package co.electriccoin.zcash.ui.screen.transactiondetails.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.transactiondetails.model.TransactionDetailsUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application = application) {
    private val _transactionDetailsUiModel = MutableStateFlow<TransactionDetailsUIModel?>(null)
    val transactionDetailsUIModel: StateFlow<TransactionDetailsUIModel?> get() = _transactionDetailsUiModel

    fun getTransactionUiModel(transactionId: Long, synchronizer: Synchronizer) {
        viewModelScope.launch(Dispatchers.IO) {
            synchronizer.transactions
                .distinctUntilChanged()
                .collectLatest { transactionSnapshotList ->
                    val transactionOverview =
                        transactionSnapshotList.find { it.id == transactionId }
                            ?: return@collectLatest
                    launch {
                        getMemoAndUpdateUiState(transactionOverview, synchronizer)
                    }
                    combine(
                        if (transactionOverview.isSentTransaction) synchronizer.getRecipients(
                            transactionOverview
                        ) else flowOf(
                            TransactionRecipient.Account(Account.DEFAULT)
                        ),
                        synchronizer.networkHeight
                    ) { transactionRecipient, networkHeight ->
                        _transactionDetailsUiModel.update {
                            it?.copy(
                                transactionOverview = transactionOverview,
                                transactionRecipient = transactionRecipient,
                                network = synchronizer.network,
                                networkHeight = networkHeight
                            ) ?: run {
                                TransactionDetailsUIModel(
                                    transactionOverview = transactionOverview,
                                    transactionRecipient = transactionRecipient,
                                    network = synchronizer.network,
                                    networkHeight = networkHeight,
                                    memo = ""
                                )
                            }
                        }
                    }.stateIn(viewModelScope)
                }
        }
    }

    val isNavigateAwayFromWarningShown = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
        emit(StandardPreferenceKeys.IS_NAVIGATE_AWAY_FROM_APP_WARNING_SHOWN.getValue(preferenceProvider = preferenceProvider))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        false
    )

    fun updateNavigateAwayFromWaringFlag(isShown: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(getApplication())
            StandardPreferenceKeys.IS_NAVIGATE_AWAY_FROM_APP_WARNING_SHOWN.putValue(preferenceProvider, isShown)
        }
    }

    private fun getMemoAndUpdateUiState(
        transactionOverview: TransactionOverview,
        synchronizer: Synchronizer
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            synchronizer.getMemos(transactionOverview).collectLatest { memo ->
                Twig.info { "memo $memo and ${transactionOverview.netValue}" }
                _transactionDetailsUiModel.update {
                    it?.copy(memo = memo) ?: TransactionDetailsUIModel(
                        null,
                        null,
                        null,
                        null,
                        memo = memo
                    )
                }
            }
        }
    }
}
