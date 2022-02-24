package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.Memo

object MemoFixture {
    const val MEMO_STRING = "Thanks for lunch"

    fun new(memoString: String = MEMO_STRING) = Memo(memoString)
}
