package cash.z.ecc.sdk.type

import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNotSame

class ZcashCurrencyTest {

    @SmallTest
    @Test
    fun check_is_zec_type() {
        assertEquals(
            ZcashCurrency.ZEC,
            ZcashCurrency.fromResources(ApplicationProvider.getApplicationContext())
        )
    }

    @SmallTest
    @Test
    fun wrong_network_type() {
        assertNotSame(
            ZcashCurrency.TAZ.network,
            ZcashCurrency.fromResources(ApplicationProvider.getApplicationContext()).network
        )
    }

    @SmallTest
    @Test
    fun check_zec_properties() {
        val zecType = ZcashCurrency.ZEC

        assertEquals(ZcashCurrency.ZEC.id, zecType.id)
        assertEquals(ZcashCurrency.ZEC.name, zecType.name)
        assertEquals(ZcashCurrency.ZEC.network, zecType.network)
    }

    @SmallTest
    @Test
    fun check_taz_properties() {
        val tazType = ZcashCurrency.TAZ

        assertEquals(ZcashCurrency.TAZ.id, tazType.id)
        assertEquals(ZcashCurrency.TAZ.name, tazType.name)
        assertEquals(ZcashCurrency.TAZ.network, tazType.network)
    }
}
