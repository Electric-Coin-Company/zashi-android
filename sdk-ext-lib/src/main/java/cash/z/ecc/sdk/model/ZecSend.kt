package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.model.Zatoshi

data class ZecSend(val destination: WalletAddress, val amount: Zatoshi, val memo: Memo) {
    companion object
}
