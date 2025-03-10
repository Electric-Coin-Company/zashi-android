package co.electriccoin.zcash.ui.screen.chooseserver

import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// TODO [#1273]: Add ChooseServer Tests #1273
// TODO [#1273]: https://github.com/Electric-Coin-Company/zashi-android/issues/1273
object AvailableServerProvider : KoinComponent {
    private val getAvailableServers: GetDefaultServersProvider by inject()

    fun getDefaultServer(): LightWalletEndpoint = getAvailableServers().first()
}
