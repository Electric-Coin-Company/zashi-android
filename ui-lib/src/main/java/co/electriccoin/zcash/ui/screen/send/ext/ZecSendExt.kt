package co.electriccoin.zcash.ui.screen.send.ext

import androidx.compose.runtime.saveable.mapSaver
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.Memo
import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.ZecSend
import kotlinx.coroutines.runBlocking

private const val KEY_ADDRESS = "address" // $NON-NLS
private const val KEY_AMOUNT = "amount" // $NON-NLS
private const val KEY_MEMO = "memo" // $NON-NLS

// Using a custom saver instead of Parcelize, to avoid adding an Android-specific API to
// the ZecSend class
internal val ZecSend.Companion.Saver
    get() = run {
        mapSaver<ZecSend?>(
            save = {
                it?.toSaverMap() ?: emptyMap()
            },
            restore = {
                if (it.isEmpty()) {
                    null
                } else {
                    val address = runBlocking { WalletAddress.Unified.new(it[KEY_ADDRESS] as String) }
                    val amount = Zatoshi(it[KEY_AMOUNT] as Long)
                    val memo = Memo(it[KEY_MEMO] as String)
                    ZecSend(address, amount, memo)
                }
            }
        )
    }

private fun ZecSend.toSaverMap() = buildMap {
    put(KEY_ADDRESS, destination.address)
    put(KEY_AMOUNT, amount.value)
    put(KEY_MEMO, memo.value)
}
