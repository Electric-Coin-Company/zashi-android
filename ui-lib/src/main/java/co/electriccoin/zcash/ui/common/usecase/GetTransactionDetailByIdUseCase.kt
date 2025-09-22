package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.SwapQuoteStatusData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GetTransactionDetailByIdUseCase(
    private val transactionRepository: TransactionRepository,
    private val addressBookRepository: AddressBookRepository,
    private val metadataRepository: MetadataRepository,
    private val swapRepository: SwapRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(txId: String) =
        channelFlow {
            val requestSwipeReloadPipeline = MutableSharedFlow<Unit>()

            val swapHandle =
                object : SwapHandle {
                    override fun requestReload() {
                        launch {
                            requestSwipeReloadPipeline.emit(Unit)
                        }
                    }
                }

            val transactionFlow =
                transactionRepository
                    .observeTransaction(txId)
                    .filterNotNull()
                    .stateIn(this)

            val memosFlow: Flow<List<String>?> =
                transactionFlow
                    .mapLatest<Transaction, List<String>?> { transactionRepository.getMemos(it) }
                    .onStart { emit(null) }
                    .distinctUntilChanged()

            val metadataFlow =
                transactionFlow
                    .distinctUntilChangedBy { it.id to it.recipient }
                    .flatMapLatest {
                        metadataRepository.observeTransactionMetadata(it)
                    }
                    .distinctUntilChanged()

            val contactFlow =
                transactionFlow
                    .flatMapLatest {
                        val address = it.recipient?.address
                        if (address == null) {
                            flowOf(null)
                        } else {
                            addressBookRepository.observeContactByAddress(address)
                        }
                    }.distinctUntilChanged()

            val swapFlow =
                requestSwipeReloadPipeline
                    .onStart { emit(Unit) }
                    .flatMapLatest {
                        metadataFlow
                            .map { it.swapMetadata }
                            .distinctUntilChanged()
                            .flatMapLatest { swapMetadata ->
                                if (swapMetadata == null) {
                                    flowOf(null)
                                } else {
                                    transactionFlow
                                        .map { it.recipient?.address }
                                        .distinctUntilChanged()
                                        .flatMapLatest { depositAddress ->
                                            if (depositAddress == null) {
                                                flowOf(null)
                                            } else {
                                                swapRepository.observeSwapStatus(depositAddress)
                                            }
                                        }
                                }
                            }
                            .distinctUntilChanged()
                    }

            combine(
                transactionFlow,
                memosFlow,
                metadataFlow,
                contactFlow,
                swapFlow
            ) { transaction, memos, metadata, contact, swap ->
                DetailedTransactionData(
                    transaction = transaction,
                    memos = memos,
                    contact = contact,
                    metadata = metadata,
                    swap = swap,
                    swapHandle = swapHandle
                )
            }.collect {
                send(it)
            }

            awaitClose {
                // do nothing
            }
        }.distinctUntilChanged().flowOn(Dispatchers.Default)
}

data class DetailedTransactionData(
    val transaction: Transaction,
    val memos: List<String>?,
    val contact: EnhancedABContact?,
    val metadata: TransactionMetadata,
    val swap: SwapQuoteStatusData?,
    val swapHandle: SwapHandle
) {
    val recipient = transaction.recipient
}

interface SwapHandle {
    fun requestReload()
}
