package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.swap.ab.AddSwapABContactArgs
import co.electriccoin.zcash.ui.screen.swap.scan.ScanSwapAddressArgs
import co.electriccoin.zcash.ui.screen.swap.scan.ScanSwapAddressArgs.Mode.SWAP_SCAN_CONTACT_ADDRESS
import co.electriccoin.zcash.ui.screen.swap.scan.ScanSwapAddressArgs.Mode.SWAP_SCAN_DESTINATION_ADDRESS
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class NavigateToScanSwapAddressUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val pipeline = MutableSharedFlow<ScanAddressPipelineResult>()

    suspend operator fun invoke(mode: ScanSwapAddressArgs.Mode): ScanResult? {
        val args = ScanSwapAddressArgs(mode)
        navigationRouter.forward(args)
        val result = pipeline.first { it.args.requestId == args.requestId }
        return when (result) {
            is ScanAddressPipelineResult.Cancelled -> null
            is ScanAddressPipelineResult.Scanned ->
                ScanResult(
                    address = result.address,
                    amount = result.amount
                )
        }
    }

    suspend fun onScanCancelled(args: ScanSwapAddressArgs) {
        pipeline.emit(ScanAddressPipelineResult.Cancelled(args))
        navigationRouter.back()
    }

    suspend fun onScanned(
        address: String,
        amount: BigDecimal?,
        args: ScanSwapAddressArgs
    ) {
        pipeline.emit(
            ScanAddressPipelineResult.Scanned(
                address = address.normalizeAddress(),
                amount = amount,
                args = args
            )
        )

        when (args.mode) {
            SWAP_SCAN_DESTINATION_ADDRESS -> navigationRouter.back()
            SWAP_SCAN_CONTACT_ADDRESS ->
                navigationRouter.replace(
                    AddSwapABContactArgs(
                        address = address,
                        chain = null
                    )
                )
        }
    }

    private fun String.normalizeAddress() = this.split(":").lastOrNull().orEmpty()
}

private sealed interface ScanAddressPipelineResult {
    val args: ScanSwapAddressArgs

    data class Cancelled(
        override val args: ScanSwapAddressArgs
    ) : ScanAddressPipelineResult

    data class Scanned(
        val address: String,
        val amount: BigDecimal?,
        override val args: ScanSwapAddressArgs
    ) : ScanAddressPipelineResult
}

data class ScanResult(
    val address: String,
    val amount: BigDecimal?
)
