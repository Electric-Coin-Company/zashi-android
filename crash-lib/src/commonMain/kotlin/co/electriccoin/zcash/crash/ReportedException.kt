package co.electriccoin.zcash.crash

import kotlinx.datetime.Instant

data class ReportedException(
    val filePath: String,
    val exceptionClassName: String,
    val isUncaught: Boolean,
    val time: Instant
) {
    companion object
}
