package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

// Magic Number doesn't matter here for hard-coded fixture values
@Suppress("MagicNumber")
object ConfigInfoFixture {
    val UPDATED_AT = "2023-01-15T08:38:45.415Z".toInstant()

    fun new(
        updatedAt: Instant? = UPDATED_AT,
    ) = ConfigInfo(updatedAt)
}
