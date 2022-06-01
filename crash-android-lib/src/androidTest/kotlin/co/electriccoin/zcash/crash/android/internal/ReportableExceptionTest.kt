package co.electriccoin.zcash.crash.android.internal

import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.crash.fixture.ReportableExceptionFixture
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportableExceptionTest {

    @Test
    fun bundle() {
        val reportableException = ReportableExceptionFixture.new()

        val bundle = reportableException.toBundle()
        val fromBundle = ReportableException.fromBundle(bundle)

        assertEquals(reportableException, fromBundle)
    }
}
