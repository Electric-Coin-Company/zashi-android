@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.about

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.about.util.WebBrowserUtil
import co.electriccoin.zcash.ui.screen.about.view.About
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapAbout(goBack: () -> Unit) {
    val walletViewModel by viewModels<WalletViewModel>()

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapAbout(
        activity = this,
        goBack = goBack,
        walletRestoringState = walletRestoringState
    )
}

@Composable
internal fun WrapAbout(
    activity: ComponentActivity,
    goBack: () -> Unit,
    walletRestoringState: WalletRestoringState,
) {
    val configInfo = ConfigInfo.new(AndroidConfigurationFactory.getInstance(activity.applicationContext))
    val versionInfo = VersionInfo.new(activity.applicationContext)

    // Allows an implicit way to force configuration refresh by simply visiting the About screen
    LaunchedEffect(key1 = true) {
        AndroidConfigurationFactory.getInstance(activity.applicationContext).hintToRefresh()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    About(
        onBack = goBack,
        versionInfo = versionInfo,
        configInfo = configInfo,
        onPrivacyPolicy = {
            openPrivacyPolicyInWebBrowser(
                activity.applicationContext,
                snackbarHostState,
                scope
            )
        },
        snackbarHostState = snackbarHostState,
        walletRestoringState = walletRestoringState,
    )
}

fun openPrivacyPolicyInWebBrowser(
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val storeIntent = WebBrowserUtil.newActivityIntent(WebBrowserUtil.ZCASH_PRIVACY_POLICY_URI)
    runCatching {
        context.startActivity(storeIntent)
    }.onFailure {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.about_unable_to_web_browser)
            )
        }
    }
}
