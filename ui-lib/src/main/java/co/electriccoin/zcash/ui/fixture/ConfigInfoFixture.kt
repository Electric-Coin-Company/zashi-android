package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo
import kotlinx.datetime.Instant

object ConfigInfoFixture {
    val UPDATED_AT = Instant.parse("2023-01-15T08:38:45.415Z")

    fun new(updatedAt: Instant? = UPDATED_AT) = ConfigInfo(updatedAt)
}
