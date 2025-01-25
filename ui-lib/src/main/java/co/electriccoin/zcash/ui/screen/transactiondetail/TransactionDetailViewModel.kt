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
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
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

class TransactionDetailViewModel(
    transactionDetail: TransactionDetail,
    getTransactionById: GetTransactionByIdUseCase,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
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
                        transactionId = stringRes(transaction.transaction.overview.txIdString()),
                        onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                        onTransactionAddressClick = { onCopyToClipboard(transaction.recipientAddress.address) },
                        fee = createFeeStringRes(transaction),
                        completedTimestamp = createTimestampStringRes(transaction),
                    )
                } else {
                    SendShieldedState(
                        contact = transaction.contact?.let { stringRes(it.name) },
                        address = createAddressStringRes(transaction),
                        transactionId = stringRes(transaction.transaction.overview.txIdString()),
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
                        transactionId = stringRes(transaction.transaction.overview.txIdString()),
                        onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                        completedTimestamp = createTimestampStringRes(transaction),
                    )
                } else {
                    ReceiveShieldedState(
                        transactionId = stringRes(transaction.transaction.overview.txIdString()),
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
                    transactionId = stringRes(transaction.transaction.overview.txIdString()),
                    onTransactionIdClick = { onCopyToClipboard(transaction.transaction.overview.txIdString()) },
                    completedTimestamp = createTimestampStringRes(transaction),
                    fee = createFeeStringRes(transaction),
                )
            }
        }

    private fun createFeeStringRes(transaction: DetailedTransactionData) =
        transaction.transaction.overview.feePaid
            ?.let {
                stringRes(R.string.transaction_detail_fee, stringRes(it))
            } ?: stringRes(R.string.transaction_detail_fee_minimal)

    private fun createAddressStringRes(transaction: DetailedTransactionData) =
        stringRes(
            transaction.recipientAddress?.address.orEmpty(),
        )

    private fun createTimestampStringRes(transaction: DetailedTransactionData) =
        transaction.transaction.overview.blockTimeEpochSeconds
            ?.let { blockTimeEpochSeconds ->
                Instant.ofEpochSecond(blockTimeEpochSeconds)
            }
            ?.atZone(ZoneId.systemDefault())
            ?.let { stringRes(it) } ?: stringRes("Pending...")

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
                        text = stringRes("Save address"),
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
                        text = stringRes("Send again"),
                        onClick = { }
                    )

                else -> null
            }
        }

    private fun onSaveAddressClick(transaction: DetailedTransactionData) {
        transaction.recipientAddress?.let {
            navigationRouter.forward(AddContactArgs(it.address))
        }
    }

    // private fun onSendAgainClick(transaction: DetailedTransactionData) {
    //
    // }

    private fun createTransactionHeaderState(transaction: DetailedTransactionData) =
        TransactionDetailHeaderState(
            title =
                when (transaction.transaction.state) {
                    SENT -> stringRes("Sent")
                    SENDING -> stringRes("Sending")
                    SEND_FAILED -> stringRes("Send failed")
                    RECEIVED -> stringRes("Received")
                    RECEIVING -> stringRes("Receiving")
                    RECEIVE_FAILED -> stringRes("Receive failed")
                    SHIELDED -> stringRes("Shielded")
                    SHIELDING -> stringRes("Shielding")
                    SHIELDING_FAILED -> stringRes("Shielding failed")
                },
            amount = stringRes(transaction.transaction.overview.netValue)
        )

    private fun onBack() {
        navigationRouter.back()
    }
}
