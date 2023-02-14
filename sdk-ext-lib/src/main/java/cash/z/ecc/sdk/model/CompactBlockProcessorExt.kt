package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.PercentDecimal

fun CompactBlockProcessor.ProcessorInfo.downloadProgress(): PercentDecimal {
    val lastDownloadRangeSnapshot = lastDownloadRange
    val lastDownloadedHeightSnapshot = lastDownloadedHeight

    return if (lastDownloadRangeSnapshot?.isEmpty() != false || lastDownloadedHeightSnapshot == null) {
        PercentDecimal.ONE_HUNDRED_PERCENT
    } else {
        val numerator = (lastDownloadedHeightSnapshot.value - lastDownloadRangeSnapshot.start.value + 1)
            .toFloat()
            .coerceAtLeast(PercentDecimal.MIN)
        val denominator = (lastDownloadRangeSnapshot.endInclusive.value - lastDownloadRangeSnapshot.start.value + 1)
            .toFloat()

        val progress = (numerator / denominator).coerceAtMost(PercentDecimal.MAX)

        PercentDecimal(progress)
    }
}

fun CompactBlockProcessor.ProcessorInfo.scanProgress(): PercentDecimal {
    val lastScanRangeSnapshot = lastScanRange
    val lastScannedHeightSnapshot = lastScannedHeight

    return if (lastScanRangeSnapshot?.isEmpty() != false || lastScannedHeightSnapshot == null) {
        PercentDecimal.ONE_HUNDRED_PERCENT
    } else {
        val numerator = (lastScannedHeightSnapshot.value - lastScanRangeSnapshot.start.value + 1)
            .toFloat()
            .coerceAtLeast(PercentDecimal.MIN)
        val demonimator = (lastScanRangeSnapshot.endInclusive.value - lastScanRangeSnapshot.start.value + 1)
            .toFloat()

        val progress = (numerator / demonimator).coerceAtMost(PercentDecimal.MAX)

        PercentDecimal(progress)
    }
}

// These are estimates
@Suppress("MagicNumber")
private val DOWNLOAD_WEIGHT = PercentDecimal(0.4f)
private val SCAN_WEIGHT = PercentDecimal(PercentDecimal.MAX - DOWNLOAD_WEIGHT.decimal)

fun CompactBlockProcessor.ProcessorInfo.totalProgress(): PercentDecimal {
    val downloadWeighted = DOWNLOAD_WEIGHT.decimal * (downloadProgress().decimal).coerceAtMost(PercentDecimal.MAX)
    val scanWeighted = SCAN_WEIGHT.decimal * (scanProgress().decimal).coerceAtMost(PercentDecimal.MAX)

    return PercentDecimal(
        downloadWeighted.coerceAtLeast(PercentDecimal.MIN) +
            scanWeighted.coerceAtLeast(PercentDecimal.MIN)
    )
}
