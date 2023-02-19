package co.electriccoin.zcash.configuration.test.fixture

import co.electriccoin.zcash.configuration.model.entry.ConfigKey
import co.electriccoin.zcash.configuration.model.entry.StringConfigurationEntry

object StringDefaultEntryFixture {
    val KEY = ConfigKey("some_string_key") // $NON-NLS
    const val DEFAULT_VALUE = "some_default_value" // $NON-NLS
    fun newEntryEntry(key: ConfigKey = KEY, value: String = DEFAULT_VALUE) = StringConfigurationEntry(key, value)
}
