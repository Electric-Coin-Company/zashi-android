package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.TransactionPool
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.Note
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVE_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SEND_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENT
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING_FAILED
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.DetailedTransactionData
import co.electriccoin.zcash.ui.common.usecase.FlipTransactionBookmarkUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.IsTransactionBookmarkUseCase
import co.electriccoin.zcash.ui.common.usecase.MarkTxMemoAsReadUseCase
import co.electriccoin.zcash.ui.common.usecase.SendTransactionAgainUseCase
import co.electriccoin.zcash.ui.common.usecase.TransactionHasNoteUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.stringResByTransactionId
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ShieldingState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemoState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailMemosState
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@Suppress("TooManyFunctions")
class TransactionDetailViewModel(
    getTransactionById: GetTransactionByIdUseCase,
    transactionHasNote: TransactionHasNoteUseCase,
    isTransactionBookmark: IsTransactionBookmarkUseCase,
    getTransactionNote: GetTransactionNoteUseCase,
    private val markTxMemoAsRead: MarkTxMemoAsReadUseCase,
    private val transactionDetail: TransactionDetail,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
    private val sendTransactionAgain: SendTransactionAgainUseCase,
    private val flipTransactionBookmark: FlipTransactionBookmarkUseCase,
) : ViewModel() {
    val state =
        combine(
            getTransactionById.observe(transactionDetail.transactionId),
            transactionHasNote.observe(transactionDetail.transactionId),
            isTransactionBookmark.observe(transactionDetail.transactionId),
            getTransactionNote.observe(transactionDetail.transactionId)
        ) { transaction, hasNote, isBookmarked, note ->
            TransactionDetailState(
                onBack = ::onBack,
                header = createTransactionHeaderState(transaction),
                info = createTransactionInfoState(transaction, note),
                primaryButton = createPrimaryButtonState(transaction),
                secondaryButton =
                    ButtonState(
                        text =
                            if (hasNote) {
                                stringRes(R.string.transaction_detail_edit_note)
                            } else {
                                stringRes(R.string.transaction_detail_add_a_note)
                            },
                        onClick = ::onAddOrEditNoteClick
                    ),
                bookmarkButton =
                    IconButtonState(
                        icon =
                            if (isBookmarked) {
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
            markTxMemoAsRead.invoke(transactionDetail.transactionId)
        }
    }

    private fun onAddOrEditNoteClick() {
        navigationRouter.forward(TransactionNote(transactionDetail.transactionId))
    }

    private fun createTransactionInfoState(
        transaction: DetailedTransactionData,
        note: Note?
    ) = when (transaction.transaction.state) {
        SENT,
        SENDING,
        SEND_FAILED -> {
            if (transaction.recipientAddress is WalletAddress.Transparent) {
                SendTransparentState(
                    contact = transaction.contact?.let { stringRes(it.name) },
                    address = createAddressStringRes(transaction),
                    addressAbbreviated = createAbbreviatedAddressStringRes(transaction),
                    transactionId =
                        stringResByTransactionId(
                            value = transaction.transaction.overview.txIdString(),
                            abbreviated = true
                        ),
                    onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                    onTransactionAddressClick = { onCopyToClipboard(transaction.recipientAddress.address) },
                    fee = createFeeStringRes(transaction),
                    completedTimestamp = createTimestampStringRes(transaction),
                    note = note?.let { stringRes(it.content) }
                )
            } else {
                SendShieldedState(
                    contact = transaction.contact?.let { stringRes(it.name) },
                    address = createAddressStringRes(transaction),
                    addressAbbreviated = createAbbreviatedAddressStringRes(transaction),
                    transactionId =
                        stringResByTransactionId(
                            value = transaction.transaction.overview.txIdString(),
                            abbreviated = true
                        ),
                    onTransactionIdClick = {
                        onCopyToClipboard(transaction.transaction.overview.txIdString())
                    },
                    onTransactionAddressClick = {
                        onCopyToClipboard(transaction.recipientAddress?.address.orEmpty())
                    },
                    fee = createFeeStringRes(transaction),
                    completedTimestamp = createTimestampStringRes(transaction),
                    memo =
                        TransactionDetailMemosState(
                            transaction.memos.orEmpty()
                                .map { memo ->
                                    TransactionDetailMemoState(
                                        content = stringRes(memo),
                                        onClick = { onCopyToClipboard(memo) }
                                    )
                                }
                        ),
                    note = note?.let { stringRes(it.content) }
                )
            }
        }

        RECEIVED,
        RECEIVING,
        RECEIVE_FAILED -> {
            if (transaction.transaction.transactionOutputs.all { it.pool == TransactionPool.TRANSPARENT }) {
                ReceiveTransparentState(
                    transactionId =
                        stringResByTransactionId(
                            value = transaction.transaction.overview.txIdString(),
                            abbreviated = true
                        ),
                    onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                    completedTimestamp = createTimestampStringRes(transaction),
                    note = note?.let { stringRes(it.content) }
                )
            } else {
                ReceiveShieldedState(
                    transactionId =
                        stringResByTransactionId(
                            value = transaction.transaction.overview.txIdString(),
                            abbreviated = true
                        ),
                    onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                    completedTimestamp = createTimestampStringRes(transaction),
                    memo =
                        TransactionDetailMemosState(
                            transaction.memos.orEmpty()
                                .map { memo ->
                                    TransactionDetailMemoState(
                                        content = stringRes(memo),
                                        onClick = { onCopyToClipboard(memo) }
                                    )
                                }
                        ),
                    note = note?.let { stringRes(it.content) }
                )
            }
        }

        SHIELDED,
        SHIELDING,
        SHIELDING_FAILED -> {
            ShieldingState(
                transactionId =
                    stringResByTransactionId(
                        value = transaction.transaction.overview.txIdString(),
                        abbreviated = true
                    ),
                onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                completedTimestamp = createTimestampStringRes(transaction),
                fee = createFeeStringRes(transaction),
                note = note?.let { stringRes(it.content) }
            )
        }
    }

    private fun createFeeStringRes(transaction: DetailedTransactionData): StringResource {
        val feePaid =
            transaction.transaction.overview.feePaid
                ?: return stringRes(R.string.transaction_detail_fee_minimal)

        return if (feePaid.value < MIN_FEE_THRESHOLD) {
            stringRes(R.string.transaction_detail_fee_minimal)
        } else {
            stringRes(R.string.transaction_detail_fee, stringRes(feePaid))
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

    private fun createTimestampStringRes(transaction: DetailedTransactionData) =
        transaction.transaction.overview.blockTimeEpochSeconds
            ?.let { blockTimeEpochSeconds ->
                Instant.ofEpochSecond(blockTimeEpochSeconds)
            }
            ?.atZone(ZoneId.systemDefault())
            ?.let {
                stringResByDateTime(
                    zonedDateTime = it,
                    useFullFormat = true
                )
            } ?: stringRes(R.string.transaction_detail_pending)

    private fun onCopyToClipboard(text: String) {
        copyToClipboard(
            tag = "Clipboard",
            value = text
        )
    }

    private fun createPrimaryButtonState(transaction: DetailedTransactionData) =
        if (transaction.contact == null) {
            when (transaction.transaction.state) {
                SENT,
                SENDING,
                SEND_FAILED ->
                    ButtonState(
                        text = stringRes(R.string.transaction_detail_save_address),
                        onClick = { onSaveAddressClick(transaction) }
                    )

                else -> null
            }
        } else {
            when (transaction.transaction.state) {
                SENT,
                SENDING,
                SEND_FAILED ->
                    ButtonState(
                        text = stringRes(R.string.transaction_detail_send_again),
                        onClick = { onSendAgainClick(transaction) }
                    )

                else -> null
            }
        }

    private fun onSaveAddressClick(transaction: DetailedTransactionData) {
        transaction.recipientAddress?.let {
            navigationRouter.forward(AddContactArgs(it.address))
        }
    }

    private fun onSendAgainClick(transaction: DetailedTransactionData) {
        sendTransactionAgain(transaction)
    }

    private fun createTransactionHeaderState(transaction: DetailedTransactionData) =
        TransactionDetailHeaderState(
            title =
                when (transaction.transaction.state) {
                    SENT -> stringRes(R.string.transaction_detail_sent)
                    SENDING -> stringRes(R.string.transaction_detail_sending)
                    SEND_FAILED -> stringRes(R.string.transaction_detail_send_failed)
                    RECEIVED -> stringRes(R.string.transaction_detail_received)
                    RECEIVING -> stringRes(R.string.transaction_detail_receiving)
                    RECEIVE_FAILED -> stringRes(R.string.transaction_detail_receive_failed)
                    SHIELDED -> stringRes(R.string.transaction_detail_shielded)
                    SHIELDING -> stringRes(R.string.transaction_detail_shielding)
                    SHIELDING_FAILED -> stringRes(R.string.transaction_detail_shielding_failed)
                },
            amount = stringRes(transaction.transaction.overview.netValue)
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
