@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.request

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cash.z.ecc.sdk.model.ZecRequest
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.request.view.Request
import kotlinx.coroutines.runBlocking

@Composable
internal fun MainActivity.WrapRequest(
    goBack: () -> Unit
) {
    WrapRequest(this, goBack)
}

@Composable
private fun WrapRequest(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = walletViewModel.addresses.collectAsState().value

    if (null == walletAddresses) {
        // Display loading indicator
    } else {
        Request(
            walletAddresses.unified,
            goBack = goBack,
            onCreateAndSend = {
                val chooserIntent = Intent.createChooser(
                    it.newShareIntent(activity.applicationContext),
                    null
                )

                activity.startActivity(chooserIntent)

                goBack()
            }
        )
    }
}

private fun ZecRequest.newShareIntent(context: Context) = runBlocking {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.request_template_format, toUri()))
        type = "text/plain"
    }
}
