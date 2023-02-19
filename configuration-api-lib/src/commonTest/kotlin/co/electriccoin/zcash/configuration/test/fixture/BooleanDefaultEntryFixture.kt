package co.electriccoin.zcash.configuration.test.fixture

import co.electriccoin.zcash.configuration.model.entry.BooleanConfigurationEntry
import co.electriccoin.zcash.configuration.model.entry.ConfigKey

object BooleanDefaultEntryFixture {

    val KEY = ConfigKey("some_boolean_key") // $NON-NLS

    fun newTrueEntry() = BooleanConfigurationEntry(KEY, true)

    fun newFalseEntry() = BooleanConfigurationEntry(KEY, false)
}
