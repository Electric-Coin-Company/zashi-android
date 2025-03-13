package co.electriccoin.zcash.crash.fixture

import co.electriccoin.zcash.crash.ReportableException
import kotlinx.datetime.Instant

object ReportableExceptionFixture {
    private val EXCEPTION = RuntimeException("I am exceptional")
    val CLASS = EXCEPTION.javaClass.name
    val TRACE = EXCEPTION.stackTraceToString()
    const val APP_VERSION = "1.0.2"
    const val IS_UNCAUGHT = true

    // No milliseconds, because those can cause some tests to fail due to rounding
    val TIMESTAMP = Instant.parse("2022-04-15T11:28:54Z")

    fun new(
        className: String = CLASS,
        trace: String = TRACE,
        appVersion: String = APP_VERSION,
        isUncaught: Boolean = IS_UNCAUGHT,
        time: Instant = TIMESTAMP
    ) = ReportableException(className, trace, appVersion, isUncaught, time)
}
