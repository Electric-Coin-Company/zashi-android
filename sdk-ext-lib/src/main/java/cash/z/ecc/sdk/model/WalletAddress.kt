package cash.z.ecc.sdk.model

sealed class WalletAddress(val address: String) {
    class Unified private constructor(address: String) : WalletAddress(address) {
        companion object {
            suspend fun new(address: String): WalletAddress.Unified {
                // https://github.com/zcash/zcash-android-wallet-sdk/issues/342
                // TODO [#342]: refactor SDK to enable direct calls for address verification
                return WalletAddress.Unified(address)
            }
        }
    }

    class Shielded private constructor(address: String) : WalletAddress(address) {
        companion object {
            suspend fun new(address: String): WalletAddress.Shielded {
                // https://github.com/zcash/zcash-android-wallet-sdk/issues/342
                // TODO [#342]: refactor SDK to enable direct calls for address verification
                return WalletAddress.Shielded(address)
            }
        }
    }

    class Transparent private constructor(address: String) : WalletAddress(address) {
        companion object {
            suspend fun new(address: String): WalletAddress.Transparent {
                // https://github.com/zcash/zcash-android-wallet-sdk/issues/342
                // TODO [#342]: refactor SDK to enable direct calls for address verification
                return WalletAddress.Transparent(address)
            }
        }
    }
}
