package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.SwapArgs

class NavigateToSwapUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter,
    // private val ephemeralAddressRepository: EphemeralAddressRepository,
    // private val isEphemeralAddressLocked: IsEphemeralAddressLockedUseCase,
    // private val createIncreaseEphemeralGapLimitProposal: CreateIncreaseEphemeralGapLimitProposalUseCase,
    // private val navigateToError: NavigateToErrorUseCase
) {
    operator fun invoke() {
        swapRepository.requestRefreshAssets()
        navigationRouter.forward(SwapArgs)
        // try {
        //     if (ephemeralAddressRepository.get() == null) {
        //         ephemeralAddressRepository.create()
        //     }
        //     when (isEphemeralAddressLocked()) {
        //         EphemeralLockState.LOCKED -> {
        //             createIncreaseEphemeralGapLimitProposal()
        //             navigationRouter.forward(SwapArgs, EphemeralLockArgs)
        //         }
        //         EphemeralLockState.UNLOCKING,
        //         EphemeralLockState.UNLOCKED -> navigationRouter.forward(SwapArgs)
        //     }
        // } catch (e: Exception) {
        //     navigateToError(ErrorArgs.General(e))
        // }
    }
}
