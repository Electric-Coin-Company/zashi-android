@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.request

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.sdk.model.ZecRequest
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.request.view.Request
import kotlinx.coroutines.runBlocking

@Composable
internal fun MainActivity.WrapRequest(goBack: () -> Unit) {
    WrapRequest(this, goBack)
}

@Composable
private fun WrapRequest(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = walletViewModel.addresses.collectAsStateWithLifecycle().value

    if (null == walletAddresses) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Request(
            walletAddresses.unified,
            goBack = goBack,
            onCreateAndSend = {
                val chooserIntent =
                    Intent.createChooser(
                        it.newShareIntent(activity.applicationContext),
                        null
                    )

                activity.startActivity(chooserIntent)

                goBack()
            }
        )
    }
}

private fun ZecRequest.newShareIntent(context: Context) =
    runBlocking {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.request_template_format, toUri()))
            type = "text/plain"
        }
    }
