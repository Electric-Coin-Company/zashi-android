package co.electriccoin.zcash.preference.test.fixture

import co.electriccoin.zcash.preference.model.entry.Key
import co.electriccoin.zcash.preference.model.entry.StringPreferenceDefault

object StringDefaultPreferenceFixture {
    val KEY = Key("some_string_key") // $NON-NLS
    const val DEFAULT_VALUE = "some_default_value" // $NON-NLS
    fun new(key: Key = KEY, value: String = DEFAULT_VALUE) = StringPreferenceDefault(key, value)
}
