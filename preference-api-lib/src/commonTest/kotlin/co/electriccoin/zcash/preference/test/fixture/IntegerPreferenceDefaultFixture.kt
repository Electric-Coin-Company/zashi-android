package co.electriccoin.zcash.preference.test.fixture

import co.electriccoin.zcash.preference.model.entry.IntegerPreferenceDefault
import co.electriccoin.zcash.preference.model.entry.Key

object IntegerPreferenceDefaultFixture {
    val KEY = Key("some_string_key") // $NON-NLS
    const val DEFAULT_VALUE = 123
    fun new(key: Key = KEY, value: Int = DEFAULT_VALUE) = IntegerPreferenceDefault(key, value)
}
