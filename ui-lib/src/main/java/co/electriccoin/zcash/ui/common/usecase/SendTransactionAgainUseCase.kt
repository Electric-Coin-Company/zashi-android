package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.send.Send

class SendTransactionAgainUseCase(
    private val prefillSendUseCase: PrefillSendUseCase,
    private val navigationRouter: NavigationRouter
) {
    operator fun invoke(value: DetailedTransactionData) {
        prefillSendUseCase.request(value)
        navigationRouter.forward(Send())
    }
}
