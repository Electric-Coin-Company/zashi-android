package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
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
import co.electriccoin.zcash.ui.common.usecase.GetTransactionByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.SendTransactionAgainUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId

@Suppress("TooManyFunctions")
class TransactionDetailViewModel(
    transactionDetail: TransactionDetail,
    getTransactionById: GetTransactionByIdUseCase,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
    private val sendTransactionAgain: SendTransactionAgainUseCase
) : ViewModel() {
    val state =
        getTransactionById.observe(transactionDetail.transactionId)
            .map { transaction ->
                TransactionDetailState(
                    onBack = ::onBack,
                    header = createTransactionHeaderState(transaction),
                    info = createTransactionInfoState(transaction),
                    primaryButton = createPrimaryButtonState(transaction),
                    secondaryButton = null
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun createTransactionInfoState(transaction: DetailedTransactionData) =
        when (transaction.transaction.state) {
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
                        memo = TransactionDetailMemoState(transaction.memos.orEmpty().map { stringRes(it) })
                    )
                }
            }

            RECEIVED,
            RECEIVING,
            RECEIVE_FAILED -> {
                if (transaction.recipientAddress is WalletAddress.Transparent) {
                    ReceiveTransparentState(
                        transactionId =
                            stringResByTransactionId(
                                value = transaction.transaction.overview.txIdString(),
                                abbreviated = true
                            ),
                        onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                        completedTimestamp = createTimestampStringRes(transaction),
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
                        memo = TransactionDetailMemoState(transaction.memos.orEmpty().map { stringRes(it) })
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
}

private const val MIN_FEE_THRESHOLD = 100000
