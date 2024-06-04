package co.electriccoin.zcash.ui.screen.seedrecovery

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.seedrecovery.view.SeedRecovery

@Composable
internal fun MainActivity.WrapSeedRecovery(
    goBack: () -> Unit,
    onDone: () -> Unit,
) {
    val walletViewModel by viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val secretState = walletViewModel.secretState.collectAsStateWithLifecycle().value

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    WrapSeedRecovery(
        activity = this,
        goBack = goBack,
        onDone = onDone,
        secretState = secretState,
        synchronizer = synchronizer,
        topAppBarSubTitleState = walletState
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapSeedRecovery(
    activity: ComponentActivity,
    goBack: () -> Unit,
    onDone: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    synchronizer: Synchronizer?,
    secretState: SecretState,
) {
    BackHandler {
        goBack()
    }

    val versionInfo = VersionInfo.new(activity.applicationContext)

    val persistableWallet =
        if (secretState is SecretState.Ready) {
            secretState.persistableWallet
        } else {
            null
        }

    if (null == synchronizer || null == persistableWallet) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        SeedRecovery(
            persistableWallet,
            onBack = goBack,
            onSeedCopy = {
                ClipboardManagerUtil.copyToClipboard(
                    activity.applicationContext,
                    activity.getString(R.string.seed_recovery_seed_clipboard_tag),
                    persistableWallet.seedPhrase.joinToString()
                )
            },
            onBirthdayCopy = {
                ClipboardManagerUtil.copyToClipboard(
                    activity.applicationContext,
                    activity.getString(R.string.seed_recovery_birthday_clipboard_tag),
                    persistableWallet.birthday?.value.toString()
                )
            },
            onDone = onDone,
            topAppBarSubTitleState = topAppBarSubTitleState,
            versionInfo = versionInfo,
        )
    }
}
