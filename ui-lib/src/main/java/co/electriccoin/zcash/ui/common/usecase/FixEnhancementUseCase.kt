package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.widget.Toast
import cash.z.ecc.android.sdk.model.TransactionId
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FixEnhancementUseCase(
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter,
    private val context: Context
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(id: String) {
        try {
            synchronizerProvider.getSynchronizer().enhanceTransaction(TransactionId.new(id))
            navigationRouter.back()
        } catch (e: Exception) {
            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
