package co.electriccoin.zcash.configuration.test.fixture

import co.electriccoin.zcash.configuration.model.entry.ConfigKey
import co.electriccoin.zcash.configuration.model.entry.IntegerConfigurationEntry

object IntegerDefaultEntryFixture {
    val KEY = ConfigKey("some_string_key") // $NON-NLS
    const val DEFAULT_VALUE = 123

    fun newEntry(
        key: ConfigKey = KEY,
        value: Int = DEFAULT_VALUE
    ) = IntegerConfigurationEntry(key, value)
}
