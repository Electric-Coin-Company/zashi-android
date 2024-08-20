package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

object ExchangeRateStateFixture {
    val STATE = ExchangeRateState.OptedOut

    fun new(state: ExchangeRateState = STATE) = state
}
