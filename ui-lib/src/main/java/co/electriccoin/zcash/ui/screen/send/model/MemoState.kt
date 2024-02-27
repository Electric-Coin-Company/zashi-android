package co.electriccoin.zcash.ui.screen.send.model

import androidx.compose.runtime.saveable.mapSaver
import cash.z.ecc.android.sdk.model.Memo

sealed class MemoState(
    open val text: String,
    open val byteSize: Int
) {
    data class Correct(
        override val text: String,
        override val byteSize: Int
    ) : MemoState(text, byteSize)

    data class TooLong(
        override val text: String,
        override val byteSize: Int
    ) : MemoState(text, byteSize)

    companion object {
        fun new(memo: String): MemoState {
            val bytesCount = Memo.countLength(memo)
            return if (bytesCount > Memo.MAX_MEMO_LENGTH_BYTES) {
                TooLong(memo, bytesCount)
            } else {
                Correct(memo, bytesCount)
            }
        }

        private const val TYPE_CORRECT = "correct" // $NON-NLS
        private const val TYPE_TOO_LONG = "too_long" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS
        private const val KEY_TEXT = "text" // $NON-NLS
        private const val KEY_LENGTH = "length" // $NON-NLS

        internal val Saver
            get() =
                run {
                    mapSaver<MemoState>(
                        save = { it.toSaverMap() },
                        restore = {
                            if (it.isEmpty()) {
                                null
                            } else {
                                val text = (it[KEY_TEXT] as String)
                                val length = (it[KEY_LENGTH] as Int)
                                val type = (it[KEY_TYPE] as String)
                                when (type) {
                                    TYPE_CORRECT -> Correct(text, length)
                                    TYPE_TOO_LONG -> TooLong(text, length)
                                    else -> null
                                }
                            }
                        }
                    )
                }

        private fun MemoState.toSaverMap(): HashMap<String, Any> {
            val saverMap = HashMap<String, Any>()
            when (this) {
                is Correct -> saverMap[KEY_TYPE] = TYPE_CORRECT
                is TooLong -> saverMap[KEY_TYPE] = TYPE_TOO_LONG
            }
            saverMap[KEY_TEXT] = this.text
            saverMap[KEY_LENGTH] = this.byteSize

            return saverMap
        }
    }
}
