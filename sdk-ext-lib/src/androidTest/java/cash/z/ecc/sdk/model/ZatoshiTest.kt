package cash.z.ecc.sdk.model

import kotlin.test.assertFailsWith

class ZatoshiTest {
    @kotlin.test.Test
    fun minValue() {
        assertFailsWith<IllegalArgumentException> {
            Zatoshi(-1)
        }
    }
}
