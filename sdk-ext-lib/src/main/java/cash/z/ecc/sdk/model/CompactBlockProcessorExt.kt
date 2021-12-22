package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.block.CompactBlockProcessor

fun CompactBlockProcessor.ProcessorInfo.downloadProgress() = if (lastDownloadRange.isEmpty()) {
    PercentDecimal.ONE_HUNDRED_PERCENT
} else {
    val numerator = (lastDownloadedHeight - lastDownloadRange.first + 1)
        .toFloat()
        .coerceAtLeast(PercentDecimal.MIN)
    val denominator = (lastDownloadRange.last - lastDownloadRange.first + 1).toFloat()

    val progress = (numerator / denominator).coerceAtMost(PercentDecimal.MAX)

    PercentDecimal(progress)
}

fun CompactBlockProcessor.ProcessorInfo.scanProgress() = if (lastScanRange.isEmpty()) {
    PercentDecimal.ONE_HUNDRED_PERCENT
} else {
    val numerator = (lastScannedHeight - lastScanRange.first + 1).toFloat().coerceAtLeast(PercentDecimal.MIN)
    val demonimator = (lastScanRange.last - lastScanRange.first + 1).toFloat()

    val progress = (numerator / demonimator).coerceAtMost(PercentDecimal.MAX)

    PercentDecimal(progress)
}

// These are estimates
private val DOWNLOAD_WEIGHT = PercentDecimal(0.4f)
private val SCAN_WEIGHT = PercentDecimal(PercentDecimal.MAX - DOWNLOAD_WEIGHT.decimal)

fun CompactBlockProcessor.ProcessorInfo.totalProgress(): PercentDecimal {
    val downloadWeighted = DOWNLOAD_WEIGHT.decimal * (downloadProgress().decimal).coerceAtMost(PercentDecimal.MAX)
    val scanWeighted = SCAN_WEIGHT.decimal * (scanProgress().decimal).coerceAtMost(PercentDecimal.MAX)

    return PercentDecimal(downloadWeighted.coerceAtLeast(PercentDecimal.MIN) + scanWeighted.coerceAtLeast(PercentDecimal.MIN))
}
