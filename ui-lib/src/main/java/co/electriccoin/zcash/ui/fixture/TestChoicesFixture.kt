package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices

object TestChoicesFixture {

    val INITIAL_CHOICES =
        mapOf(
            Pair(Index(0), "baz"),
            Pair(Index(1), "foo"),
        )

    fun new(initial: Map<Index, String?> = INITIAL_CHOICES) = TestChoices(initial)
}
