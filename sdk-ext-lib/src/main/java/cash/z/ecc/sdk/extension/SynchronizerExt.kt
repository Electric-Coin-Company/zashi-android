@file:Suppress("ktlint:standard:filename")

package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.ZecSend

// TODO [#1285]: Adopt proposal API
// TODO [#1285]: https://github.com/Electric-Coin-Company/zashi-android/issues/1285
@Suppress("deprecation")
suspend fun Synchronizer.send(
    spendingKey: UnifiedSpendingKey,
    send: ZecSend
) = sendToAddress(
    spendingKey,
    send.amount,
    send.destination.address,
    send.memo.value
)
