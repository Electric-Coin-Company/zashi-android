package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.TransactionMetadata
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

class GetTransactionDetailByIdUseCase(
    private val transactionRepository: TransactionRepository,
    private val addressBookRepository: AddressBookRepository,
    private val metadataRepository: MetadataRepository,
    private val synchronizerProvider: SynchronizerProvider,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(txId: String) = combine(
        transactionRepository.observeTransaction(txId).filterNotNull(),
        metadataRepository.observeTransactionMetadataByTxId(txId)
    ) { transaction, metadata ->
        transaction to metadata
    }.flatMapLatest { (transaction, metadata) ->
        flow {
            emit(
                DetailedTransactionData(
                    transaction = transaction,
                    memos = null,
                    contact = null,
                    recipientAddress = null,
                    metadata = metadata
                )
            )
            val memos = transaction.let { transactionRepository.getMemos(it) }
            val recipientAddress = getWalletAddress(transactionRepository.getRecipients(transaction))
            emit(
                DetailedTransactionData(
                    transaction = transaction,
                    memos = memos,
                    contact = null,
                    recipientAddress = recipientAddress,
                    metadata = metadata
                )
            )
            emitAll(
                addressBookRepository
                    .observeContactByAddress(recipientAddress?.address.orEmpty())
                    .mapLatest { contact ->
                        DetailedTransactionData(
                            transaction = transaction,
                            memos = memos,
                            contact = contact,
                            recipientAddress = recipientAddress,
                            metadata = metadata
                        )
                    }
            )
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
    val transaction: TransactionData,
    val memos: List<String>?,
    val contact: AddressBookContact?,
    val recipientAddress: WalletAddress?,
    val metadata: TransactionMetadata?
) {
    val hasNoteMetadata: Boolean
        get() = metadata?.noteMetadata?.isNotEmpty() == true

    val isBookmarked: Boolean
        get() = metadata?.isBookmark == true
}
