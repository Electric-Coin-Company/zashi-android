package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo

object UpdateInfoFixture {

    const val INITIAL_PRIORITY = 0
    const val INITIAL_FORCE = true

    fun new(
        priority: Int = INITIAL_PRIORITY,
        force: Boolean = INITIAL_FORCE
    ) = UpdateInfo(priority, force)
}
