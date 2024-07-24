@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.about

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.configuration.api.ConfigurationProvider
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.about.util.WebBrowserUtil
import co.electriccoin.zcash.ui.screen.about.view.About
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun WrapAbout(
    goBack: () -> Unit,
    goWhatsNew: () -> Unit,
) {
    val activity = LocalActivity.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    BackHandler {
        goBack()
    }

    val androidConfigurationProvider = koinInject<ConfigurationProvider>()
    val configInfo = ConfigInfo.new(androidConfigurationProvider)
    val versionInfo = VersionInfo.new(activity.applicationContext)

    // Allows an implicit way to force configuration refresh by simply visiting the About screen
    LaunchedEffect(key1 = true) {
        androidConfigurationProvider.hintToRefresh()
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
        topAppBarSubTitleState = walletState,
        onWhatsNew = goWhatsNew
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
