package co.electriccoin.zcash.ui.common.serialization.metada

import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.serialization.METADATA_ENCRYPTION_KEY_SIZE
import co.electriccoin.zcash.ui.common.serialization.METADATA_FILE_IDENTIFIER_SIZE
import co.electriccoin.zcash.ui.common.serialization.METADATA_SALT_SIZE
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.aead.ChaCha20Poly1305Key
import com.google.crypto.tink.subtle.Hkdf
import com.google.crypto.tink.util.SecretBytes

/**
 * The long-term key that can decrypt an account's encrypted address book.
 */
class MetadataKey(
    val encryptionBytes: SecretBytes,
    val decryptionBytes: SecretBytes?
) {
    /**
     * Derives the filename that this key is able to decrypt.
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun fileIdentifier(): String {
        val access = InsecureSecretKeyAccess.get()
        val fileIdentifier =
            Hkdf.computeHkdf(
                "HMACSHA256",
                encryptionBytes.toByteArray(access),
                null,
                "file_identifier".toByteArray(),
                METADATA_FILE_IDENTIFIER_SIZE
            )
        return "zashi-metadata-" + fileIdentifier.toHexString()
    }

    fun deriveEncryptionKey(salt: ByteArray): ChaCha20Poly1305Key {
        assert(salt.size == METADATA_SALT_SIZE)
        val access = InsecureSecretKeyAccess.get()
        val subKey =
            Hkdf.computeHkdf(
                "HMACSHA256",
                encryptionBytes.toByteArray(access),
                null,
                salt + "encryption_key".toByteArray(),
                METADATA_ENCRYPTION_KEY_SIZE
            )
        return ChaCha20Poly1305Key.create(SecretBytes.copyFrom(subKey, access))
    }

    fun deriveFirstDecryptionKey(salt: ByteArray): ChaCha20Poly1305Key {
        return deriveDecryptionkey(salt, encryptionBytes)
    }

    fun deriveSecondDecryptionKey(salt: ByteArray): ChaCha20Poly1305Key? {
        if (decryptionBytes == null) return null
        return deriveDecryptionkey(salt, decryptionBytes)
    }

    private fun deriveDecryptionkey(
        salt: ByteArray,
        decryptionBytes: SecretBytes
    ): ChaCha20Poly1305Key {
        assert(salt.size == METADATA_SALT_SIZE)
        val access = InsecureSecretKeyAccess.get()
        val subKey =
            Hkdf.computeHkdf(
                "HMACSHA256",
                decryptionBytes.toByteArray(access),
                null,
                salt + "decryption_key".toByteArray(),
                METADATA_ENCRYPTION_KEY_SIZE
            )
        return ChaCha20Poly1305Key.create(SecretBytes.copyFrom(subKey, access))
    }

    companion object {
        suspend fun derive(
            seedPhrase: SeedPhrase,
            network: ZcashNetwork,
            zashiAccount: ZashiAccount,
            selectedAccount: WalletAccount
        ): MetadataKey {
            val key =
                DerivationTool.getInstance()
                    .deriveAccountMetadataKey(
                        seed = seedPhrase.toByteArray(),
                        network = network,
                        accountIndex = zashiAccount.hdAccountIndex,
                    )
                    .derivePrivateUseMetadataKey(
                        ufvk =
                            when (selectedAccount) {
                                is KeystoneAccount -> selectedAccount.sdkAccount.ufvk
                                is ZashiAccount -> null
                            },
                        network = network,
                        privateUseSubject = "metadata".toByteArray()
                    )
            return MetadataKey(
                encryptionBytes = SecretBytes.copyFrom(key[0], InsecureSecretKeyAccess.get()),
                decryptionBytes =
                    key.getOrNull(1)
                        ?.let {
                            SecretBytes.copyFrom(it, InsecureSecretKeyAccess.get())
                        }
            )
        }
    }
}
