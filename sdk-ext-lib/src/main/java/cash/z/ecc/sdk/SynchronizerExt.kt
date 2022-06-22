@file:Suppress("ktlint:filename")

package cash.z.ecc.sdk

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.sdk.model.ZecSend

fun Synchronizer.send(spendingKey: String, send: ZecSend) = sendToAddress(
    spendingKey,
    send.amount.value,
    send.destination.address,
    send.memo.value
)
