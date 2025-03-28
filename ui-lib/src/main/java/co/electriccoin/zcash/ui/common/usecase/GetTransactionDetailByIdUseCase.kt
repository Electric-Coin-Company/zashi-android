package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class GetTransactionDetailByIdUseCase(
    private val transactionRepository: TransactionRepository,
    private val addressBookRepository: AddressBookRepository,
    private val metadataRepository: MetadataRepository,
    private val synchronizerProvider: SynchronizerProvider,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(txId: String) =
        channelFlow {
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

            combine(
                transactionFlow,
                addressFlow,
                memosFlow,
                metadataFlow,
                contactFlow
            ) { transaction, address, memos, metadata, contact ->
                DetailedTransactionData(
                    transaction = transaction,
                    memos = memos,
                    contact = contact,
                    recipientAddress = address,
                    metadata = metadata
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
    val contact: AddressBookContact?,
    val recipientAddress: WalletAddress?,
    val metadata: TransactionMetadata
)
