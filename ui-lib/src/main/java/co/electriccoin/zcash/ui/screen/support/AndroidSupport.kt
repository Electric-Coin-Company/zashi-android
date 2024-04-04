@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.support

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.support.model.SupportInfo
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.screen.support.view.Support
import co.electriccoin.zcash.ui.screen.support.viewmodel.SupportViewModel
import co.electriccoin.zcash.ui.util.EmailUtil
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSupport(goBack: () -> Unit) {
    val supportViewModel by viewModels<SupportViewModel>()

    val walletViewModel by viewModels<WalletViewModel>()

    val supportInfo = supportViewModel.supportInfo.collectAsStateWithLifecycle().value

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapSupport(
        activity = this,
        goBack = goBack,
        supportInfo = supportInfo,
        walletRestoringState = walletRestoringState
    )
}

@Composable
internal fun WrapSupport(
    activity: ComponentActivity,
    goBack: () -> Unit,
    supportInfo: SupportInfo?,
    walletRestoringState: WalletRestoringState,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val (isShowingDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }

    Support(
        snackbarHostState = snackbarHostState,
        isShowingDialog = isShowingDialog,
        setShowDialog = setShowDialog,
        onBack = goBack,
        onSend = { userMessage ->
            val fullMessage = formatMessage(userMessage, supportInfo)

            val mailIntent =
                EmailUtil.newMailActivityIntent(
                    activity.getString(R.string.support_email_address),
                    activity.getString(R.string.app_name),
                    fullMessage
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

            runCatching {
                activity.startActivity(mailIntent)
            }.onSuccess {
                setShowDialog(false)
            }.onFailure {
                setShowDialog(false)
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = activity.getString(R.string.support_unable_to_open_email)
                    )
                }
            }
        },
        walletRestoringState = walletRestoringState
    )
}

// Note that we don't need to localize this format string
private fun formatMessage(
    messageBody: String,
    appInfo: SupportInfo?,
    supportInfoValues: Set<SupportInfoType> = SupportInfoType.entries.toSet()
): String = "$messageBody\n\n${appInfo?.toSupportString(supportInfoValues) ?: ""}"
