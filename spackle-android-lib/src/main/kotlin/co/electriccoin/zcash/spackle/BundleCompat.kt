package co.electriccoin.zcash.spackle

import android.os.Bundle
import java.io.Serializable

@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.getSerializableCompat(key: String): T? =
    if (AndroidApiVersion.isAtLeastTiramisu) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
