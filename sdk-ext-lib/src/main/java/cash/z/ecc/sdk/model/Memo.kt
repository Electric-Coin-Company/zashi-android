package cash.z.ecc.sdk.model

import cash.z.ecc.sdk.ext.sizeInUtf8Bytes

@JvmInline
value class Memo(val value: String) {
    init {
        require(isWithinMaxLength(value)) {
            "Memo length in bytes must be less than $MAX_MEMO_LENGTH_BYTES but " +
                "actually has length ${value.sizeInUtf8Bytes()}"
        }
    }

    companion object {
        /**
         * The decoded memo contents MUST NOT exceed 512 bytes.
         *
         * https://zips.z.cash/zip-0321
         */
        private const val MAX_MEMO_LENGTH_BYTES = 512

        fun isWithinMaxLength(memoString: String) = memoString.sizeInUtf8Bytes() <= MAX_MEMO_LENGTH_BYTES
    }
}
