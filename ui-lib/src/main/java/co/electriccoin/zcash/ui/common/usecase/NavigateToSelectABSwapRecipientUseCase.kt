package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.repository.BiometricsCancelledException
import co.electriccoin.zcash.ui.common.repository.BiometricsFailureException
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.swap.ab.SelectABSwapRecipientArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class NavigateToSelectABSwapRecipientUseCase(
    private val navigationRouter: NavigationRouter,
    private val biometricRepository: BiometricRepository
) {
    private val pipeline = MutableSharedFlow<SelectSwapRecipientPipelineResult>()

    suspend operator fun invoke(): EnhancedABContact? =
        try {
            biometricRepository.requestBiometrics(
                BiometricRequest(
                    message =
                        stringRes(
                            R.string.authentication_system_ui_subtitle,
                            stringRes(R.string.authentication_use_case_adress_book)
                        )
                )
            )
            val args = SelectABSwapRecipientArgs()
            navigationRouter.forward(args)
            val result = pipeline.first { it.args.requestId == args.requestId }
            when (result) {
                is SelectSwapRecipientPipelineResult.Cancelled -> null
                is SelectSwapRecipientPipelineResult.Scanned -> result.contact
            }
        } catch (_: BiometricsFailureException) {
            null
        } catch (_: BiometricsCancelledException) {
            null
        }

    suspend fun onSelectionCancelled(args: SelectABSwapRecipientArgs) {
        pipeline.emit(SelectSwapRecipientPipelineResult.Cancelled(args))
        navigationRouter.back()
    }

    suspend fun onSelected(contact: EnhancedABContact, args: SelectABSwapRecipientArgs) {
        pipeline.emit(SelectSwapRecipientPipelineResult.Scanned(contact = contact, args = args))
        navigationRouter.back()
    }
}

private sealed interface SelectSwapRecipientPipelineResult {
    val args: SelectABSwapRecipientArgs

    data class Cancelled(
        override val args: SelectABSwapRecipientArgs
    ) : SelectSwapRecipientPipelineResult

    data class Scanned(
        val contact: EnhancedABContact,
        override val args: SelectABSwapRecipientArgs
    ) : SelectSwapRecipientPipelineResult
}
