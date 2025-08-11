package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.TransactionPool
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.SendTransaction
import co.electriccoin.zcash.ui.common.repository.ShieldTransaction
import co.electriccoin.zcash.ui.common.repository.SwapQuoteStatusData
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.DetailedTransactionData
import co.electriccoin.zcash.ui.common.usecase.FlipTransactionBookmarkUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionDetailByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.MarkTxMemoAsReadUseCase
import co.electriccoin.zcash.ui.common.usecase.SendTransactionAgainUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.SwapQuoteHeaderState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
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
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
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
class TransactionDetailVM(
    getTransactionDetailById: GetTransactionDetailByIdUseCase,
    private val markTxMemoAsRead: MarkTxMemoAsReadUseCase,
    private val transactionDetailArgs: TransactionDetailArgs,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
    private val sendTransactionAgain: SendTransactionAgainUseCase,
    private val flipTransactionBookmark: FlipTransactionBookmarkUseCase,
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
                TransactionDetailState(
                    onBack = ::onBack,
                    header = createTransactionHeaderState(transaction),
                    info = createTransactionInfoState(transaction),
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
                            quoteHeader = createQuoteHeaderState(transaction.swap),
                            depositAddress = stringResByAddress(
                                value = transaction.recipientAddress?.address.orEmpty(),
                                abbreviated = true
                            ),
                            recipientAddress = recipient?.let { stringResByAddress(it, abbreviated = true) },
                            transactionId = stringResByTransactionId(
                                value = transaction.transaction.id.txIdString(),
                                abbreviated = true
                            ),
                            onTransactionIdClick = {
                                onCopyToClipboard(transaction.transaction.id.txIdString())
                            },
                            onDepositAddressClick = {
                                onCopyToClipboard(transaction.recipientAddress?.address.orEmpty())
                            },
                            onRecipientAddressClick = if (recipient == null) {
                                null
                            } else {
                                { onCopyToClipboard(recipient) }
                            },
                            fee = createFeeStringRes(transaction),
                            isSlippageRealized = transaction.swap.data?.isSlippageRealized == true,
                            maxSlippage = transaction.swap.data?.maxSlippage?.let {
                                stringResByNumber(it, 0) + stringRes("%")
                            },
                            note = transaction.metadata.note?.let { stringRes(it) },
                            refundedAmount = transaction.swap.data?.refundedFormatted
                                ?.let {
                                    stringResByDynamicCurrencyNumber(amount = it, ticker = "ZEC")
                                }
                                ?.takeIf {
                                    transaction.swap.data.status == SwapStatus.REFUNDED
                                }
                        )
                    }

                    transaction.recipientAddress is WalletAddress.Transparent -> SendTransparentState(
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

                    else -> SendShieldedState(
                        contact = transaction.contact?.let { stringRes(it.name) },
                        address = createAbbreviatedAddressStringRes(transaction),
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
                        memo = TransactionDetailMemosState(
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
                        memo = TransactionDetailMemosState(
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

    private fun createQuoteHeaderState(swap: SwapQuoteStatusData): SwapQuoteHeaderState {
        fun createFromState(): SwapTokenAmountState? =
            when (swap.data?.swapMode) {
                EXACT_INPUT -> {
                    SwapTokenAmountState(
                        bigIcon = imageRes(R.drawable.ic_zec_round_full),
                        smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                        title = stringRes(swap.data.amountInZatoshi, HIDDEN),
                        subtitle = stringResByDynamicCurrencyNumber(swap.data.amountInUsd, FiatCurrency.USD.symbol)
                    )
                }

                EXACT_OUTPUT ->
                    SwapTokenAmountState(
                        bigIcon = swap.destinationAsset?.tokenIcon,
                        smallIcon = swap.destinationAsset?.chainIcon,
                        title = stringResByDynamicNumber(swap.data.amountOutFormatted),
                        subtitle = stringResByDynamicCurrencyNumber(swap.data.amountOutUsd, FiatCurrency.USD.symbol)
                    )

                null -> null
            }

        fun createToState(): SwapTokenAmountState? =
            when (swap.data?.swapMode) {
                EXACT_INPUT ->
                    SwapTokenAmountState(
                        bigIcon = swap.destinationAsset?.tokenIcon,
                        smallIcon = swap.destinationAsset?.chainIcon,
                        title = stringResByDynamicNumber(swap.data.amountOutFormatted),
                        subtitle = stringResByDynamicCurrencyNumber(swap.data.amountOutUsd, FiatCurrency.USD.symbol)
                    )

                EXACT_OUTPUT ->
                    SwapTokenAmountState(
                        bigIcon = imageRes(R.drawable.ic_zec_round_full),
                        smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                        title = stringRes(swap.data.amountInZatoshi, HIDDEN),
                        subtitle = stringResByDynamicCurrencyNumber(swap.data.amountInUsd, FiatCurrency.USD.symbol)
                    )

                null -> null
            }

        return SwapQuoteHeaderState(
            rotateIcon = swap.data?.swapMode?.let { it == EXACT_OUTPUT },
            from = createFromState(),
            to = createToState(),
        )
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

    private fun createErrorFooter(data: DetailedTransactionData): ErrorFooter? {
        if (data.swap?.error == null) return null

        val isServiceUnavailableError =
            data.swap.error is ResponseException &&
                data.swap.error.response.status == HttpStatusCode.ServiceUnavailable

        return ErrorFooter(
            title =
                if (isServiceUnavailableError) {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_service_unavailable)
                } else {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_unexpected_error)
                },
            subtitle =
                if (isServiceUnavailableError) {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_please_try_again)
                } else {
                    stringRes(co.electriccoin.zcash.ui.design.R.string.general_check_connection)
                }
        )
    }

    private fun createPrimaryButtonState(data: DetailedTransactionData): ButtonState? {
        return when {
            data.swap?.error != null -> {
                val isServiceUnavailableError =
                    data.swap.error is ResponseException &&
                        data.swap.error.response.status == HttpStatusCode.ServiceUnavailable

                if (isServiceUnavailableError) {
                    null
                } else {
                    ButtonState(
                        text = stringRes(co.electriccoin.zcash.ui.design.R.string.general_try_again),
                        onClick = { data.swapHandle.requestReload() },
                        style = ButtonStyle.DESTRUCTIVE1
                    )
                }
            }

            data.swap != null -> null

            data.contact == null -> {
                if (data.transaction is SendTransaction) {
                    ButtonState(
                        text = stringRes(R.string.transaction_detail_save_address),
                        onClick = { onSaveAddressClick(data) }
                    )
                } else {
                    null
                }
            }

            else -> {
                if (data.transaction is SendTransaction) {
                    ButtonState(
                        text = stringRes(R.string.transaction_detail_send_again),
                        onClick = { onSendAgainClick(data) }
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun createSecondaryButtonState(transaction: DetailedTransactionData): ButtonState? {
        fun createAddNoteButtonState() = ButtonState(
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
            flipTransactionBookmark(transactionDetailArgs.transactionId)
        }
}

private const val MIN_FEE_THRESHOLD = 100000
