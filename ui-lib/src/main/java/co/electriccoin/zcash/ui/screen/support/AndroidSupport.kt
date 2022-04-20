package co.electriccoin.zcash.ui.screen.support

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.support.model.SupportInfo
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.screen.support.util.EmailUtil
import co.electriccoin.zcash.ui.screen.support.view.Support
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSupport(
    goBack: () -> Unit
) {
    WrapSupport(this, goBack)
}

@Composable
internal fun WrapSupport(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val viewModel by activity.viewModels<SupportViewModel>()
    val supportMessage = viewModel.supportInfo.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Support(
        snackbarHostState,
        onBack = goBack,
        onSend = { userMessage ->
            val fullMessage = formatMessage(userMessage, supportMessage)

            val mailIntent = EmailUtil.newMailActivityIntent(
                activity.getString(R.string.support_email_address),
                activity.getString(R.string.app_name),
                fullMessage
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // TODO [#386] This should only fail if there's no email app, e.g. on a TV device
            runCatching {
                activity.startActivity(mailIntent)
            }.onSuccess {
                goBack()
            }.onFailure {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = activity.getString(R.string.support_unable_to_open_email)
                    )
                }
            }
        }
    )
}

// Note that we don't need to localize this format string
private fun formatMessage(
    messageBody: String,
    appInfo: SupportInfo?,
    supportInfoValues: Set<SupportInfoType> = SupportInfoType.values().toSet()
): String = "$messageBody\n\n${appInfo?.toSupportString(supportInfoValues) ?: ""}"
