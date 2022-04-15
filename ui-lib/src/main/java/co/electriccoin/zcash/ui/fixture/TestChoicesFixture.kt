package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices

object TestChoicesFixture {
    fun new(initial: Map<Index, String?>) = TestChoices(initial)
}
