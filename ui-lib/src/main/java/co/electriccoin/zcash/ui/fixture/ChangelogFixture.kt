package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.common.model.Changelog

// Magic Number doesn't matter here for hard-coded fixture values
@Suppress("MagicNumber")
object ChangelogFixture {
    const val VERSION = 1
    const val ADDED = ""

    fun new(
        version: Int = VERSION,
        added: String = ADDED
    ) = Changelog(
        version = version,
        date = added,
        added = added,
        changed = added,
        fixed = added,
        removed = added,
    )
}
