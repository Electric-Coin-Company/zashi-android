package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.common.model.Changelog
import co.electriccoin.zcash.ui.common.model.ChangelogSection
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

object ChangelogFixture {
    const val VERSION = "1.0.0"
    val DATE = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val ADDED = ChangelogSection(title = "Added", content = "Added lorem ipsum")
    val CHANGED = ChangelogSection(title = "Changed", content = "Changed lorem ipsum")
    val FIXED = ChangelogSection(title = "Fixed", content = "Fixed lorem ipsum")
    val REMOVED = ChangelogSection(title = "Removed", content = "Removed lorem ipsum")

    @Suppress("LongParameterList")
    fun new(
        version: String = VERSION,
        date: LocalDate = DATE,
        added: ChangelogSection = ADDED,
        changed: ChangelogSection = CHANGED,
        fixed: ChangelogSection = FIXED,
        removed: ChangelogSection = REMOVED,
    ) = Changelog(
        version = version,
        date = date,
        added = added,
        changed = changed,
        fixed = fixed,
        removed = removed,
    )
}
