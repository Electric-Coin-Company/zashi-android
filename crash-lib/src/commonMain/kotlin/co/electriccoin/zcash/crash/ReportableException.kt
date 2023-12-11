package co.electriccoin.zcash.crash

import kotlinx.datetime.Instant

data class ReportableException(
    val exceptionClass: String,
    val exceptionTrace: String,
    val appVersion: String,
    val isUncaught: Boolean,
    val time: Instant
) {
    companion object
}
