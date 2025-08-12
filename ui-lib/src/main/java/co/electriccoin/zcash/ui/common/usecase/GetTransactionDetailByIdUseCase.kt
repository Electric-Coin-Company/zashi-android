package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.SwapQuoteStatusData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.combine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val synchronizerProvider: SynchronizerProvider,
    private val swapRepository: SwapRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(txId: String) =
        channelFlow {
            val requestSwipeReloadPipeline = MutableSharedFlow<Unit>()

            val swapHandle = object : SwapHandle {
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

            val addressFlow =
                transactionFlow
                    .mapLatest { getWalletAddress(transactionRepository.getRecipients(it)) }
                    .onStart { emit(null) }
                    .distinctUntilChanged()

            val memosFlow: Flow<List<String>?> =
                transactionFlow
                    .mapLatest<Transaction, List<String>?> { transactionRepository.getMemos(it) }
                    .onStart { emit(null) }
                    .distinctUntilChanged()

            val metadataFlow =
                metadataRepository
                    .observeTransactionMetadataByTxId(txId)

            val contactFlow =
                addressFlow
                    .flatMapLatest { addressBookRepository.observeContactByAddress(it?.address.orEmpty()) }
                    .distinctUntilChanged()

            val swapFlow = requestSwipeReloadPipeline
                .onStart { emit(Unit) }
                .flatMapLatest {
                    metadataFlow
                        .map { it.swapMetadata }
                        .distinctUntilChanged()
                        .flatMapLatest { swapMetadata ->
                            if (swapMetadata == null) {
                                flowOf(null)
                            } else {
                                addressFlow
                                    .filterNotNull()
                                    .flatMapLatest { swapRepository.observeSwapStatus(it.address) }
                            }
                        }
                }

            combine(
                transactionFlow,
                addressFlow,
                memosFlow,
                metadataFlow,
                contactFlow,
                swapFlow
            ) { transaction, address, memos, metadata, contact, swap ->
                DetailedTransactionData(
                    transaction = transaction,
                    memos = memos,
                    contact = contact,
                    recipientAddress = address,
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

    private suspend fun getWalletAddress(address: String?): WalletAddress? {
        if (address == null) return null

        return when (synchronizerProvider.getSynchronizer().validateAddress(address)) {
            AddressType.Shielded -> WalletAddress.Sapling.new(address)
            AddressType.Tex -> WalletAddress.Tex.new(address)
            AddressType.Transparent -> WalletAddress.Transparent.new(address)
            AddressType.Unified -> WalletAddress.Unified.new(address)
            else -> null
        }
    }
}

data class DetailedTransactionData(
    val transaction: Transaction,
    val memos: List<String>?,
    val contact: EnhancedABContact?,
    val recipientAddress: WalletAddress?,
    val metadata: TransactionMetadata,
    val swap: SwapQuoteStatusData?,
    val swapHandle: SwapHandle
)

interface SwapHandle {
    fun requestReload()
}
