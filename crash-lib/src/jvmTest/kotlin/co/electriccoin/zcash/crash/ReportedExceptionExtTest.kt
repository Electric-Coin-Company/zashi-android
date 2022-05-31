package co.electriccoin.zcash.crash

import co.electriccoin.zcash.crash.fixture.ReportableExceptionFixture
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReportedExceptionExtTest {
    @Test
    fun only_txt_files() {
        assertEquals(ReportedException.new(File("something.txt.tmp")), null)
    }

    @Test
    fun able_to_parse() {
        val filename = ExceptionPath.newExceptionFileName(ReportableExceptionFixture.new())

        val parsed = ReportedException.new(File(filename))

        assertNotNull(parsed)

        assertEquals(ReportableExceptionFixture.CLASS, parsed.exceptionClassName)
        // Note the timestamp is rounded to the nearest second
        assertEquals(ReportableExceptionFixture.TIMESTAMP, parsed.time)
        assertEquals(filename, parsed.filePath)
        assertEquals(ReportableExceptionFixture.IS_UNCAUGHT, parsed.isUncaught)
    }
}
