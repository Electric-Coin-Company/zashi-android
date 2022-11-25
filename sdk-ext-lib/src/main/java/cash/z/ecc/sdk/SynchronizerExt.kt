@file:Suppress("ktlint:filename")

package cash.z.ecc.sdk

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.sdk.model.ZecSend

suspend fun Synchronizer.send(spendingKey: UnifiedSpendingKey, send: ZecSend) = sendToAddress(
    spendingKey,
    send.amount,
    send.destination.address,
    send.memo.value
)
