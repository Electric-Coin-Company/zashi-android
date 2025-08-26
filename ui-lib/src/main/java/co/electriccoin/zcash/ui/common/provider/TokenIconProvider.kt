package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes

interface TokenIconProvider {
    fun getIcon(ticker: String): ImageResource
}

class TokenIconProviderImpl(
    private val context: Context
) : TokenIconProvider {
    override fun getIcon(ticker: String): ImageResource {
        val normalized = ticker.removePrefix("$").lowercase()

        val id =
            context.resources.getIdentifier(
                "ic_token_$normalized",
                "drawable",
                context.packageName
            )

        return if (id == 0) imageRes(R.drawable.ic_token_placeholder) else imageRes(id)
    }
}
