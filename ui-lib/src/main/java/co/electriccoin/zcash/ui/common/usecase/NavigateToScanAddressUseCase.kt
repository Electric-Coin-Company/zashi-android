package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.contact.AddSwapContactArgs
import co.electriccoin.zcash.ui.screen.scan.swap.ScanAddressArgs
import co.electriccoin.zcash.ui.screen.scan.swap.ScanAddressArgs.Mode.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class NavigateToScanAddressUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val pipeline = MutableSharedFlow<ScanAddressPipelineResult>()

    suspend operator fun invoke(mode: ScanAddressArgs.Mode): ScanResult? {
        val args = ScanAddressArgs(mode)
        navigationRouter.forward(args)
        val result = pipeline.first { it.args.requestId == args.requestId }
        return when (result) {
            is ScanAddressPipelineResult.Cancelled -> null
            is ScanAddressPipelineResult.Scanned -> ScanResult(
                address = result.address,
                amount = result.amount
            )
        }
    }

    suspend fun onScanCancelled(args: ScanAddressArgs) {
        pipeline.emit(ScanAddressPipelineResult.Cancelled(args))
        navigationRouter.back()
    }

    suspend fun onScanned(
        address: String,
        amount: BigDecimal?,
        args: ScanAddressArgs
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
            SWAP_SCAN_CONTACT_ADDRESS -> navigationRouter.replace(
                AddSwapContactArgs(
                    address = address,
                    chain = null
                )
            )
        }
    }

    private fun String.normalizeAddress() = this.split(":").lastOrNull().orEmpty()
}

private sealed interface ScanAddressPipelineResult {

    val args: ScanAddressArgs

    data class Cancelled(
        override val args: ScanAddressArgs
    ) : ScanAddressPipelineResult

    data class Scanned(
        val address: String,
        val amount: BigDecimal?,
        override val args: ScanAddressArgs
    ) : ScanAddressPipelineResult
}

data class ScanResult(val address: String, val amount: BigDecimal?)
