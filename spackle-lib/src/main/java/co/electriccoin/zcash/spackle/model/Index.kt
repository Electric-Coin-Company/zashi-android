package co.electriccoin.zcash.spackle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Useful for accessing arrays or lists by index.
 *
 * @param value A 0-based index.  Must be >= 0
 */
@JvmInline
@Parcelize
value class Index(val value: Int) : Parcelable {
    init {
        require(value >= 0) { "Index must be >= 0 but actually is $value" }
    }
}
