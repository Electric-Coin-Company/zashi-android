package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.IsTorExplicitlySetProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class IsTorEnabledUseCase(
    private val persistableWalletProvider: PersistableWalletProvider,
    private val isTorExplicitlySetProvider: IsTorExplicitlySetProvider
) {
    fun observe() =
        combine(
            persistableWalletProvider.persistableWallet,
            isTorExplicitlySetProvider.observe()
        ) { wallet, isTorExplicitlyEnabled -> wallet?.isTorEnabled?.takeIf { isTorExplicitlyEnabled } }
            .distinctUntilChanged()
}
