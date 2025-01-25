package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GetTransactionByIdUseCase(
    private val transactionRepository: TransactionRepository,
    private val addressBookRepository: AddressBookRepository,
    private val synchronizerProvider: SynchronizerProvider,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(txId: String): Flow<DetailedTransactionData> =
        transactionRepository.currentTransactions
            .filterNotNull()
            .flatMapLatest { transactions ->
                channelFlow {
                    val memosData = MutableStateFlow<List<String>?>(null)
                    val contactData = MutableStateFlow<AddressBookContact?>(null)

                    val transaction = transactions.find { tx -> tx.overview.txIdString() == txId }

                    if (transaction != null) {
                        val recipientAddress = getWalletAddress(transactionRepository.getRecipients(transaction))
                        val contact =
                            recipientAddress?.let {
                                addressBookRepository.getContactByAddress(it.address)
                            }
                        contactData.update { contact }

                        launch {
                            combine(memosData, contactData) { memos, contact ->
                                memos to contact
                            }.collect { (memos, contact) ->
                                send(
                                    DetailedTransactionData(
                                        transaction = transaction,
                                        memos = memos,
                                        contact = contact,
                                        recipientAddress = recipientAddress
                                    )
                                )
                            }
                        }

                        val memos = transaction.let { transactionRepository.getMemos(it) }
                        memosData.update { memos }

                        if (recipientAddress != null) {
                            launch {
                                addressBookRepository
                                    .observeContactByAddress(recipientAddress.address)
                                    .collect { new ->
                                        contactData.update { new }
                                    }
                            }
                        }
                    }

                    awaitClose {
                        // do nothing
                    }
                }
            }
            .distinctUntilChanged()

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
    val recipientAddress: WalletAddress?
)
