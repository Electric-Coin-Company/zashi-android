package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.TransactionPool
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.model.SwapStatus.EXPIRED
import co.electriccoin.zcash.ui.common.model.SwapStatus.FAILED
import co.electriccoin.zcash.ui.common.model.SwapStatus.INCOMPLETE_DEPOSIT
import co.electriccoin.zcash.ui.common.model.SwapStatus.PENDING
import co.electriccoin.zcash.ui.common.model.SwapStatus.PROCESSING
import co.electriccoin.zcash.ui.common.model.SwapStatus.REFUNDED
import co.electriccoin.zcash.ui.common.model.SwapStatus.SUCCESS
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
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.loadingImageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.design.util.stringResByTransactionId
import co.electriccoin.zcash.ui.screen.contact.AddZashiABContactArgs
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.ReceiveTransparentState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendShieldedState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.SendSwapState
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

@Suppress("TooManyFunctions")
class TransactionDetailVM(
    getTransactionDetailById: GetTransactionDetailByIdUseCase,
    private val markTxMemoAsRead: MarkTxMemoAsReadUseCase,
    private val transactionDetailArgs: TransactionDetailArgs,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
    private val sendTransactionAgain: SendTransactionAgainUseCase,
    private val flipTransactionBookmark: FlipTransactionBookmarkUseCase,
    private val mapper: CommonTransactionDetailMapper
) : ViewModel() {
    private val transaction =
        getTransactionDetailById
            .observe(transactionDetailArgs.transactionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        transaction
            .filterNotNull()
            .mapLatest { transaction ->
                var info = createTransactionInfoState(transaction)
                TransactionDetailState(
                    onBack = ::onBack,
                    info = info,
                    header = createTransactionHeaderState(transaction, info),
                    primaryButton = createPrimaryButtonState(transaction),
                    secondaryButton = createSecondaryButtonState(transaction),
                    bookmarkButton =
                        IconButtonState(
                            icon =
                                if (transaction.metadata.isBookmarked) {
                                    R.drawable.ic_transaction_detail_bookmark
                                } else {
                                    R.drawable.ic_transaction_detail_no_bookmark
                                },
                            onClick = ::onBookmarkClick
                        ),
                    errorFooter = createErrorFooter(transaction)
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
                    markTxMemoAsRead(transactionDetailArgs.transactionId)
                }
            }
        }
    }

    private fun onAddOrEditNoteClick() {
        navigationRouter.forward(TransactionNote(transactionDetailArgs.transactionId))
    }

    @Suppress("CyclomaticComplexMethod")
    private fun createTransactionInfoState(transaction: DetailedTransactionData): TransactionDetailInfoState =
        when (transaction.transaction) {
            is SendTransaction -> {
                when {
                    transaction.swap != null -> {
                        val recipient = transaction.swap.data?.recipient
                        SendSwapState(
                            status = transaction.swap.data?.status,
                            quoteHeader =
                                mapper.createTransactionDetailQuoteHeaderState(
                                    swap = transaction.swap.data,
                                    originAsset = transaction.swap.originAsset,
                                    destinationAsset = transaction.swap.destinationAsset
                                ),
                            depositAddress =
                                stringResByAddress(
                                    value = transaction.recipient?.address.orEmpty(),
                                    abbreviated = true
                                ),
                            totalFees =
                                transaction.metadata.swapMetadata
                                    ?.totalFees
                                    ?.let { stringRes(it) },
                            recipientAddress = recipient?.let { stringResByAddress(it, abbreviated = true) },
                            transactionId =
                                stringResByTransactionId(
                                    value = transaction.transaction.id.txIdString(),
                                    abbreviated = true
                                ),
                            refundedAmount =
                                transaction.swap.data
                                    ?.refundedFormatted
                                    ?.let {
                                        stringResByCurrencyNumber(amount = it, ticker = "ZEC")
                                    }?.takeIf {
                                        transaction.swap.data.status == SwapStatus.REFUNDED
                                    },
                            onTransactionIdClick = {
                                onCopyToClipboard(transaction.transaction.id.txIdString())
                            },
                            onDepositAddressClick = {
                                onCopyToClipboard(transaction.recipient?.address.orEmpty())
                            },
                            onRecipientAddressClick =
                                if (recipient == null) {
                                    null
                                } else {
                                    { onCopyToClipboard(recipient) }
                                },
                            maxSlippage =
                                transaction.swap.data?.maxSlippage?.let {
                                    stringResByNumber(it, 0) + stringRes("%")
                                },
                            note = transaction.metadata.note?.let { stringRes(it) },
                            isSlippageRealized = transaction.swap.data?.isSlippageRealized == true,
                            isPending = isPending(transaction),
                            completedTimestamp = createTimestampStringRes(transaction),
                        )
                    }

                    transaction.recipient is WalletAddress.Transparent ->
                        SendTransparentState(
                            contact = transaction.contact?.let { stringRes(it.name) },
                            address = stringResByAddress(transaction.recipient.address, false),
                            addressAbbreviated = stringResByAddress(transaction.recipient.address, true),
                            transactionId =
                                stringResByTransactionId(
                                    value = transaction.transaction.id.txIdString(),
                                    abbreviated = true
                                ),
                            onTransactionIdClick = {
                                onCopyToClipboard(transaction.transaction.id.txIdString())
                            },
                            onTransactionAddressClick = { onCopyToClipboard(transaction.recipient.address) },
                            fee = createFeeStringRes(transaction),
                            completedTimestamp = createTimestampStringRes(transaction),
                            isPending = isPending(transaction),
                            note = transaction.metadata.note?.let { stringRes(it) },
                        )

                    else ->
                        SendShieldedState(
                            contact = transaction.contact?.let { stringRes(it.name) },
                            address =
                                stringResByAddress(
                                    value = transaction.recipient?.address.orEmpty(),
                                    abbreviated = true
                                ),
                            transactionId =
                                stringResByTransactionId(
                                    value = transaction.transaction.id.txIdString(),
                                    abbreviated = true
                                ),
                            onTransactionIdClick = {
                                onCopyToClipboard(transaction.transaction.id.txIdString())
                            },
                            onTransactionAddressClick = {
                                onCopyToClipboard(transaction.recipient?.address.orEmpty())
                            },
                            fee = createFeeStringRes(transaction),
                            completedTimestamp = createTimestampStringRes(transaction),
                            memo =
                                TransactionDetailMemosState(
                                    transaction.memos?.map { memo ->
                                        TransactionDetailMemoState(
                                            content = stringRes(memo),
                                            onClick = { onCopyToClipboard(memo) }
                                        )
                                    }
                                ).takeIf { transaction.transaction.memoCount > 0 },
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
                            TransactionDetailMemosState(
                                transaction.memos?.map { memo ->
                                    TransactionDetailMemoState(
                                        content = stringRes(memo),
                                        onClick = { onCopyToClipboard(memo) }
                                    )
                                }
                            ).takeIf { transaction.transaction.memoCount > 0 },
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

    private fun createTimestampStringRes(data: DetailedTransactionData) =
        mapper.createTransactionDetailTimestamp(data.transaction.timestamp)

    private fun isPending(data: DetailedTransactionData) = data.transaction.timestamp == null

    private fun onCopyToClipboard(text: String) {
        copyToClipboard(
            tag = "Clipboard",
            value = text
        )
    }

    private fun createErrorFooter(data: DetailedTransactionData): ErrorFooter? =
        mapper.createTransactionDetailErrorFooter(data.swap?.error)

    private fun createPrimaryButtonState(data: DetailedTransactionData): ButtonState? =
        when {
            data.swap?.error != null && data.swap.data != null ->
                mapper.createTransactionDetailErrorButtonState(
                    error = data.swap.error,
                    reloadHandle = data.reloadHandle
                )

            data.swap != null -> null

            data.contact == null ->
                if (data.transaction is SendTransaction) {
                    ButtonState(
                        text = stringRes(R.string.transaction_detail_save_address),
                        onClick = { onSaveAddressClick(data) }
                    )
                } else {
                    null
                }

            else ->
                if (data.transaction is SendTransaction) {
                    ButtonState(
                        text = stringRes(R.string.transaction_detail_send_again),
                        onClick = { onSendAgainClick(data) }
                    )
                } else {
                    null
                }
        }

    private fun createSecondaryButtonState(transaction: DetailedTransactionData): ButtonState? {
        fun createAddNoteButtonState() =
            ButtonState(
                text =
                    if (transaction.metadata.note != null) {
                        stringRes(R.string.transaction_detail_edit_note)
                    } else {
                        stringRes(R.string.transaction_detail_add_a_note)
                    },
                onClick = ::onAddOrEditNoteClick
            )

        return when {
            transaction.swap != null && transaction.swap.error == null -> createAddNoteButtonState()
            transaction.swap != null -> null
            else -> createAddNoteButtonState()
        }
    }

    private fun onSaveAddressClick(transaction: DetailedTransactionData) {
        transaction.recipient?.let {
            navigationRouter.forward(AddZashiABContactArgs(it.address))
        }
    }

    private fun onSendAgainClick(transaction: DetailedTransactionData) {
        sendTransactionAgain(transaction)
    }

    private fun createTransactionHeaderState(
        data: DetailedTransactionData,
        info: TransactionDetailInfoState
    ): TransactionDetailHeaderState {
        return TransactionDetailHeaderState(
            title = when (val transaction = data.transaction) {
                is ReceiveTransaction.Success -> stringRes(R.string.transaction_history_received)
                is ReceiveTransaction.Pending -> stringRes(R.string.transaction_detail_receiving)
                is ReceiveTransaction.Failed -> stringRes(R.string.transaction_history_receiving_failed)
                is ShieldTransaction.Success -> stringRes(R.string.transaction_history_shielded)
                is ShieldTransaction.Pending -> stringRes(R.string.transaction_detail_shielding)
                is ShieldTransaction.Failed -> stringRes(R.string.transaction_history_shielding_failed)
                is SendTransaction -> {
                    if (data.metadata.swapMetadata == null) {
                        when (transaction) {
                            is SendTransaction.Success -> stringRes(R.string.transaction_history_sent)
                            is SendTransaction.Pending -> stringRes(R.string.transaction_detail_sending)
                            is SendTransaction.Failed -> stringRes(R.string.transaction_history_sending_failed)
                        }
                    } else {
                        if (transaction is SendTransaction.Failed) {
                            when (data.metadata.swapMetadata.mode) {
                                EXACT_INPUT -> stringRes(R.string.transaction_history_swap_failed)
                                EXACT_OUTPUT -> stringRes(R.string.transaction_history_payment_failed)
                            }
                        } else {
                            when (data.metadata.swapMetadata.mode) {
                                EXACT_INPUT ->
                                    when (data.metadata.swapMetadata.status) {
                                        INCOMPLETE_DEPOSIT,
                                        PROCESSING,
                                        PENDING -> stringRes(R.string.transaction_detail_swapping)

                                        SUCCESS -> stringRes(R.string.transaction_history_swapped)
                                        REFUNDED -> stringRes(R.string.transaction_history_swap_refunded)
                                        FAILED -> stringRes(R.string.transaction_history_swap_failed)
                                        EXPIRED -> stringRes(R.string.transaction_history_swap_expired)
                                    }

                                EXACT_OUTPUT ->
                                    when (data.metadata.swapMetadata.status) {
                                        INCOMPLETE_DEPOSIT,
                                        PROCESSING,
                                        PENDING -> stringRes(R.string.transaction_detail_paying)

                                        SUCCESS -> stringRes(R.string.transaction_history_paid)
                                        REFUNDED -> stringRes(R.string.transaction_history_payment_refunded)
                                        FAILED -> stringRes(R.string.transaction_history_payment_failed)
                                        EXPIRED -> stringRes(R.string.transaction_history_payment_expired)
                                    }
                            }
                        }
                    }
                }
            },
            amount =
                stringRes(data.transaction.amount, HIDDEN),
            icons =
                when (info) {
                    is ReceiveShieldedState,
                    is ReceiveTransparentState -> {
                        listOf(
                            imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
                            imageRes(R.drawable.ic_transaction_received)
                        )
                    }

                    is SendSwapState -> listOf(
                        data.metadata.swapMetadata?.origin?.tokenIcon ?: loadingImageRes(),
                        when (data.metadata.swapMetadata?.mode) {
                            SwapMode.EXACT_INPUT -> imageRes(R.drawable.ic_transaction_sent)
                            SwapMode.EXACT_OUTPUT -> imageRes(R.drawable.ic_transaction_paid)
                            null -> imageRes(R.drawable.ic_transaction_sent)
                        },
                        data.metadata.swapMetadata?.destination?.tokenIcon ?: loadingImageRes()
                    )

                    is SendShieldedState,
                    is SendTransparentState -> listOf(
                        imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
                        imageRes(R.drawable.ic_transaction_sent)
                    )

                    is ShieldingState -> listOf(
                        imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
                        imageRes(R.drawable.ic_transaction_shielded),
                        imageRes(R.drawable.ic_transaction_detail_shielded),
                    )
                }
        )
    }

    private fun onBack() = navigationRouter.back()

    private fun onBookmarkClick() =
        viewModelScope.launch {
            flipTransactionBookmark(transactionDetailArgs.transactionId)
        }
}

private const val MIN_FEE_THRESHOLD = 100000
