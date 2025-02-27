package co.electriccoin.zcash.ui.common.serialization

import com.google.crypto.tink.aead.ChaCha20Poly1305Key

interface Key {
    fun fileIdentifier(): String

    fun deriveEncryptionKey(salt: ByteArray): ChaCha20Poly1305Key
}
