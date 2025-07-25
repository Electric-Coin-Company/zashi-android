package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.TransactionPool
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.SendTransaction
import co.electriccoin.zcash.ui.common.repository.ShieldTransaction
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.DetailedTransactionData
import co.electriccoin.zcash.ui.common.usecase.FlipTransactionBookmarkUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionDetailByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.MarkTxMemoAsReadUseCase
import co.electriccoin.zcash.ui.common.usecase.SendTransactionAgainUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.stringResByTransactionId
import co.electriccoin.zcash.ui.screen.contact.AddZashiABContactArgs
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ShieldingState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailInfoState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemoState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemosState
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId

@Suppress("TooManyFunctions")
class TransactionDetailViewModel(
    getTransactionDetailById: GetTransactionDetailByIdUseCase,
    private val markTxMemoAsRead: MarkTxMemoAsReadUseCase,
    private val transactionDetail: TransactionDetail,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
    private val sendTransactionAgain: SendTransactionAgainUseCase,
    private val flipTransactionBookmark: FlipTransactionBookmarkUseCase,
) : ViewModel() {
    private val transaction =
        getTransactionDetailById
            .observe(transactionDetail.transactionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        transaction
            .filterNotNull()
            .mapLatest { transaction ->
                TransactionDetailState(
                    onBack = ::onBack,
                    header = createTransactionHeaderState(transaction),
                    info = createTransactionInfoState(transaction),
                    primaryButton = createPrimaryButtonState(transaction),
                    secondaryButton =
                        ButtonState(
                            text =
                                if (transaction.metadata.note != null) {
                                    stringRes(R.string.transaction_detail_edit_note)
                                } else {
                                    stringRes(R.string.transaction_detail_add_a_note)
                                },
                            onClick = ::onAddOrEditNoteClick
                        ),
                    bookmarkButton =
                        IconButtonState(
                            icon =
                                if (transaction.metadata.isBookmarked) {
                                    R.drawable.ic_transaction_detail_bookmark
                                } else {
                                    R.drawable.ic_transaction_detail_no_bookmark
                                },
                            onClick = ::onBookmarkClick
                        )
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val transaction = transaction.filterNotNull().first()
                if (transaction.transaction.memoCount > 0) {
                    markTxMemoAsRead(transactionDetail.transactionId)
                }
            }
        }
    }

    private fun onAddOrEditNoteClick() {
        navigationRouter.forward(TransactionNote(transactionDetail.transactionId))
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createTransactionInfoState(transaction: DetailedTransactionData): TransactionDetailInfoState =
        when (transaction.transaction) {
            is SendTransaction -> {
                if (transaction.recipientAddress is WalletAddress.Transparent) {
                    SendTransparentState(
                        contact = transaction.contact?.let { stringRes(it.name) },
                        address = createAddressStringRes(transaction),
                        addressAbbreviated = createAbbreviatedAddressStringRes(transaction),
                        transactionId =
                            stringResByTransactionId(
                                value = transaction.transaction.id.txIdString(),
                                abbreviated = true
                            ),
                        onTransactionIdClick = {
                            onCopyToClipboard(transaction.transaction.id.txIdString())
                        },
                        onTransactionAddressClick = { onCopyToClipboard(transaction.recipientAddress.address) },
                        fee = createFeeStringRes(transaction),
                        completedTimestamp = createTimestampStringRes(transaction),
                        note = transaction.metadata.note?.let { stringRes(it) },
                        isPending = isPending(transaction)
                    )
                } else {
                    SendShieldedState(
                        contact = transaction.contact?.let { stringRes(it.name) },
                        address = createAddressStringRes(transaction),
                        addressAbbreviated = createAbbreviatedAddressStringRes(transaction),
                        transactionId =
                            stringResByTransactionId(
                                value = transaction.transaction.id.txIdString(),
                                abbreviated = true
                            ),
                        onTransactionIdClick = {
                            onCopyToClipboard(transaction.transaction.id.txIdString())
                        },
                        onTransactionAddressClick = {
                            onCopyToClipboard(transaction.recipientAddress?.address.orEmpty())
                        },
                        fee = createFeeStringRes(transaction),
                        completedTimestamp = createTimestampStringRes(transaction),
                        memo =
                            transaction.memos?.let {
                                TransactionDetailMemosState(
                                    it.map { memo ->
                                        TransactionDetailMemoState(
                                            content = stringRes(memo),
                                            onClick = { onCopyToClipboard(memo) }
                                        )
                                    }
                                )
                            },
                        note = transaction.metadata.note?.let { stringRes(it) },
                        isPending = isPending(transaction)
                    )
                }
            }

            is ReceiveTransaction -> {
                if (transaction.transaction.transactionOutputs.all { it.pool == TransactionPool.TRANSPARENT }) {
                    ReceiveTransparentState(
                        transactionId =
                            stringResByTransactionId(
                                value = transaction.transaction.id.txIdString(),
                                abbreviated = true
                            ),
                        onTransactionIdClick = {
                            onCopyToClipboard(transaction.transaction.id.txIdString())
                        },
                        completedTimestamp = createTimestampStringRes(transaction),
                        note = transaction.metadata.note?.let { stringRes(it) },
                        isPending = isPending(transaction)
                    )
                } else {
                    ReceiveShieldedState(
                        transactionId =
                            stringResByTransactionId(
                                value = transaction.transaction.id.txIdString(),
                                abbreviated = true
                            ),
                        onTransactionIdClick = {
                            onCopyToClipboard(transaction.transaction.id.txIdString())
                        },
                        completedTimestamp = createTimestampStringRes(transaction),
                        memo =
                            transaction.memos?.let {
                                TransactionDetailMemosState(
                                    it.map { memo ->
                                        TransactionDetailMemoState(
                                            content = stringRes(memo),
                                            onClick = { onCopyToClipboard(memo) }
                                        )
                                    }
                                )
                            },
                        note = transaction.metadata.note?.let { stringRes(it) },
                        isPending = isPending(transaction)
                    )
                }
            }

            is ShieldTransaction -> {
                ShieldingState(
                    transactionId =
                        stringResByTransactionId(
                            value = transaction.transaction.id.txIdString(),
                            abbreviated = true
                        ),
                    onTransactionIdClick = {
                        onCopyToClipboard(transaction.transaction.id.txIdString())
                    },
                    completedTimestamp = createTimestampStringRes(transaction),
                    fee = createFeeStringRes(transaction),
                    note = transaction.metadata.note?.let { stringRes(it) },
                    isPending = isPending(transaction)
                )
            }
        }

    private fun createFeeStringRes(data: DetailedTransactionData): StringResource {
        val feePaid =
            data.transaction.fee.takeIf { data.transaction !is ReceiveTransaction }
                ?: return stringRes(R.string.transaction_detail_fee_minimal)

        return if (feePaid.value < MIN_FEE_THRESHOLD) {
            stringRes(R.string.transaction_detail_fee_minimal)
        } else {
            stringRes(R.string.transaction_detail_fee, stringRes(feePaid, HIDDEN))
        }
    }

    private fun createAddressStringRes(transaction: DetailedTransactionData) =
        stringResByAddress(
            value = transaction.recipientAddress?.address.orEmpty(),
            abbreviated = false
        )

    private fun createAbbreviatedAddressStringRes(transaction: DetailedTransactionData) =
        stringResByAddress(
            value = transaction.recipientAddress?.address.orEmpty(),
            abbreviated = true
        )

    private fun createTimestampStringRes(data: DetailedTransactionData) =
        data.transaction.timestamp
            ?.atZone(ZoneId.systemDefault())
            ?.let {
                stringResByDateTime(
                    zonedDateTime = it,
                    useFullFormat = true
                )
            } ?: stringRes(R.string.transaction_detail_pending)

    private fun isPending(data: DetailedTransactionData) = data.transaction.timestamp == null

    private fun onCopyToClipboard(text: String) {
        copyToClipboard(
            tag = "Clipboard",
            value = text
        )
    }

    private fun createPrimaryButtonState(data: DetailedTransactionData) =
        if (data.contact == null) {
            if (data.transaction is SendTransaction) {
                ButtonState(
                    text = stringRes(R.string.transaction_detail_save_address),
                    onClick = { onSaveAddressClick(data) }
                )
            } else {
                null
            }
        } else {
            if (data.transaction is SendTransaction) {
                ButtonState(
                    text = stringRes(R.string.transaction_detail_send_again),
                    onClick = { onSendAgainClick(data) }
                )
            } else {
                null
            }
        }

    private fun onSaveAddressClick(transaction: DetailedTransactionData) {
        transaction.recipientAddress?.let {
            navigationRouter.forward(AddZashiABContactArgs(it.address))
        }
    }

    private fun onSendAgainClick(transaction: DetailedTransactionData) {
        sendTransactionAgain(transaction)
    }

    private fun createTransactionHeaderState(data: DetailedTransactionData) =
        TransactionDetailHeaderState(
            title =
                when (data.transaction) {
                    is SendTransaction.Success -> stringRes(R.string.transaction_detail_sent)
                    is SendTransaction.Pending -> stringRes(R.string.transaction_detail_sending)
                    is SendTransaction.Failed -> stringRes(R.string.transaction_detail_send_failed)
                    is ReceiveTransaction.Success -> stringRes(R.string.transaction_detail_received)
                    is ReceiveTransaction.Pending -> stringRes(R.string.transaction_detail_receiving)
                    is ReceiveTransaction.Failed -> stringRes(R.string.transaction_detail_receive_failed)
                    is ShieldTransaction.Success -> stringRes(R.string.transaction_detail_shielded)
                    is ShieldTransaction.Pending -> stringRes(R.string.transaction_detail_shielding)
                    is ShieldTransaction.Failed -> stringRes(R.string.transaction_detail_shielding_failed)
                },
            amount =
                stringRes(data.transaction.amount, HIDDEN)
        )

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onBookmarkClick() =
        viewModelScope.launch {
            flipTransactionBookmark(transactionDetail.transactionId)
        }
}

private const val MIN_FEE_THRESHOLD = 100000
