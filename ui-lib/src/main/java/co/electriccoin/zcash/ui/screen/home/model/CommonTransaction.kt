package co.electriccoin.zcash.ui.screen.home.model

import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.PendingTransaction
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.Zatoshi

data class CommonTransaction(
    val value: Zatoshi?,
    val fee: Zatoshi?,
    val minedHeight: BlockHeight?,
    val expiryHeight: BlockHeight?,
    val rawTransactionId: FirstClassByteArray?,
    val raw: FirstClassByteArray?,
    val memoCount: Int?,
    val memo: FirstClassByteArray?,
    val index: Long?,
    val isSentTransaction: Boolean,
    val isChange: Boolean,
    val isWalletInternal: Boolean,
    val receivedNoteCount: Int?,
    val sentNoteCount: Int?,
    val blockTimeEpochSeconds: Long?,
    val recipient: TransactionRecipient?,
    val sentFromAccount: Account?,
    val cancelled: Int?,
    val encodeAttempts: Int?,
    val submitAttempts: Int?,
    val errorMessage: String?,
    val errorCode: Int?,
    val createTime: Long?
) {
    constructor(pendingTransaction: PendingTransaction) : this(
        value = pendingTransaction.value,
        fee = pendingTransaction.fee,
        minedHeight = pendingTransaction.minedHeight,
        expiryHeight = pendingTransaction.expiryHeight,
        rawTransactionId = pendingTransaction.rawTransactionId,
        raw = pendingTransaction.raw,
        memoCount = null,
        memo = pendingTransaction.memo,
        index = null,
        isSentTransaction = false,
        isChange = false,
        isWalletInternal = false,
        receivedNoteCount = null,
        sentNoteCount = null,
        blockTimeEpochSeconds = null,
        recipient = pendingTransaction.recipient,
        sentFromAccount = pendingTransaction.sentFromAccount,
        cancelled = pendingTransaction.cancelled,
        encodeAttempts = pendingTransaction.encodeAttempts,
        submitAttempts = pendingTransaction.submitAttempts,
        errorMessage = pendingTransaction.errorMessage,
        errorCode = pendingTransaction.errorCode,
        createTime = pendingTransaction.createTime
    )

    constructor(transactionOverview: TransactionOverview) : this(
        value = transactionOverview.netValue,
        fee = transactionOverview.feePaid,
        minedHeight = transactionOverview.minedHeight,
        expiryHeight = transactionOverview.expiryHeight,
        rawTransactionId = transactionOverview.rawId,
        raw = transactionOverview.raw,
        memoCount = transactionOverview.memoCount,
        memo = null,
        index = transactionOverview.index,
        isSentTransaction = transactionOverview.isSentTransaction,
        isChange = transactionOverview.isChange,
        isWalletInternal = transactionOverview.isWalletInternal,
        receivedNoteCount = transactionOverview.receivedNoteCount,
        sentNoteCount = transactionOverview.sentNoteCount,
        blockTimeEpochSeconds = transactionOverview.blockTimeEpochSeconds,
        recipient = null,
        sentFromAccount = null,
        cancelled = null,
        encodeAttempts = null,
        submitAttempts = null,
        errorMessage = null,
        errorCode = null,
        createTime = null
    )
}
