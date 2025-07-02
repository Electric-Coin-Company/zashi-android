package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.scan.swap.ScanAddressArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class NavigateToScanAddressUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val pipeline = MutableSharedFlow<PipelineResult>()

    suspend operator fun invoke(): ScanResult? {
        val args = ScanAddressArgs()
        navigationRouter.forward(args)
        val result = pipeline.first { it.args.requestId == args.requestId }
        return when (result) {
            is PipelineResult.Cancelled -> null
            is PipelineResult.Scanned -> ScanResult(
                address = result.address,
                amount = result.amount
            )
        }
    }

    suspend fun onScanCancelled(args: ScanAddressArgs) {
        pipeline.emit(PipelineResult.Cancelled(args))
        navigationRouter.back()
    }

    suspend fun onScanned(
        address: String,
        amount: BigDecimal?,
        args: ScanAddressArgs
    ) {
        pipeline.emit(
            PipelineResult.Scanned(
                address = address.normalizeAddress(),
                amount = amount,
                args = args
            )
        )
        navigationRouter.back()
    }

    private fun String.normalizeAddress() = this.split(":").lastOrNull().orEmpty()
}

private sealed interface PipelineResult {

    val args: ScanAddressArgs

    data class Cancelled(
        override val args: ScanAddressArgs
    ) : PipelineResult

    data class Scanned(
        val address: String,
        val amount: BigDecimal?,
        override val args: ScanAddressArgs
    ) : PipelineResult
}

data class ScanResult(val address: String, val amount: BigDecimal?)
