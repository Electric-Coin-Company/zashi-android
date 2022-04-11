package cash.z.ecc.sdk.model

data class ZecSend(val destination: WalletAddress, val amount: Zatoshi, val memo: Memo) {
    companion object
}
