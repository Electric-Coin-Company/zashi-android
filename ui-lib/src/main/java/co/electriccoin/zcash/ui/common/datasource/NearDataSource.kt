package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.NearTokenChain
import co.electriccoin.zcash.ui.common.provider.ChainIconProvider
import co.electriccoin.zcash.ui.common.provider.ChainNameProvider
import co.electriccoin.zcash.ui.common.provider.NearApiProvider
import co.electriccoin.zcash.ui.common.provider.TokenIconProvider
import co.electriccoin.zcash.ui.common.provider.TokenNameProvider
import kotlinx.io.IOException

interface NearDataSource {
    @Throws(IOException::class)
    suspend fun getSupportedTokens(): List<NearTokenChain>
}

class NearDataSourceImpl(
    private val chainIconProvider: ChainIconProvider,
    private val chainNameProvider: ChainNameProvider,
    private val tokenIconProvider: TokenIconProvider,
    private val tokenNameProvider: TokenNameProvider,
    private val nearApiProvider: NearApiProvider,
) : NearDataSource {
    override suspend fun getSupportedTokens(): List<NearTokenChain> =
        nearApiProvider.getSupportedTokens().map {
            NearTokenChain(
                token = it,
                tokenName = tokenNameProvider.getName(it.symbol),
                tokenIcon = tokenIconProvider.getIcon(it.symbol),
                chainName = chainNameProvider.getName(it.blockchain),
                chainIcon = chainIconProvider.getIcon(it.blockchain)
            )
        }
}
