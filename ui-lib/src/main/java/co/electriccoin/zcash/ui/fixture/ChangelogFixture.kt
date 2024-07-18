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
    val ADDED =
        ChangelogSection(
            title = "Added",
            content =
                "\n- Highly requested dark mode functionality added. Turn it on by switching into dark mode in your " +
                    "device settings. Enjoy!" +
                    "\n- Scan QR code from an image stored in your photo library." +
                    "\n- Security feature added - hide your balances and transaction history with an eye icon on the " +
                    "Account and Balances tabs."
        )
    val CHANGED =
        ChangelogSection(
            title = "Changed",
            content =
                "\n- The copy on the confirmation button of the secret recovery phrase screen has been modified" +
                    "\n- We also improved Ul on the Receive screen - you can now switch between the unified and " +
                    "transparent address."
        )
    val FIXED =
        ChangelogSection(
            title = "Fixed",
            content = "\n- Balances are refreshed right after the send or shielding transaction are processed."
        )
    val REMOVED =
        ChangelogSection(
            title = "Removed",
            content = "\n- The privacy policy link is no longer displayed on the About screen."
        )

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
