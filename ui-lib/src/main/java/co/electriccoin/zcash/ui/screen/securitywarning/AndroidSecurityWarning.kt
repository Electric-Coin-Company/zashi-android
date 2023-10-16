package co.electriccoin.zcash.ui.screen.securitywarning

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo
import co.electriccoin.zcash.ui.screen.securitywarning.util.WebBrowserUtil
import co.electriccoin.zcash.ui.screen.securitywarning.view.SecurityWarning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapSecurityWarning(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    WrapSecurityWarning(
        this,
        onBack = onBack,
        onConfirm = onConfirm
    )
}

@Composable
internal fun WrapSecurityWarning(
    activity: ComponentActivity,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val packageInfo = activity.packageManager.getPackageInfoCompat(activity.packageName, 0L)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SecurityWarning(
        snackbarHostState = snackbarHostState,
        versionInfo = VersionInfo.new(packageInfo),
        onBack = onBack,
        onAcknowledged = {
            // Needed for UI testing only
        },
        onPrivacyPolicy = {
            openPrivacyPolicyInWebBrowser(
                activity.applicationContext,
                snackbarHostState,
                scope
            )
        },
        onConfirm = onConfirm
    )

    LaunchedEffect(key1 = true) {
        AndroidConfigurationFactory.getInstance(activity.applicationContext).hintToRefresh()
    }
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
                message = context.getString(R.string.security_warning_unable_to_web_browser)
            )
        }
    }
}
