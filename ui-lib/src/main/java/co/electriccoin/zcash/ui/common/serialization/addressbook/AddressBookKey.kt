package co.electriccoin.zcash.ui.common.serialization.addressbook

import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import com.google.crypto.tink.util.SecretBytes

/**
 * The long-term key that can decrypt an account's encrypted address book.
 */
class AddressBookKey(val key: SecretBytes) {
    companion object {
        suspend fun derive(
            seedPhrase: SeedPhrase,
            network: ZcashNetwork,
            account: Account
        ): AddressBookKey {
            TODO()
        }
    }
}
