package co.electriccoin.zcash.ui.screen.pin

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.authenticate
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.pin.view.AuthenticatePin
import co.electriccoin.zcash.ui.screen.pin.view.SetUpPin
import co.electriccoin.zcash.ui.screen.pin.viewmodel.PinViewModel

@Composable
internal fun MainActivity.AndroidPin(isPinSetup: Boolean = false, onBack: () -> Unit) {
    WrapAndroidPin(activity = this, isPinSetup = isPinSetup, onBack = onBack)
}

@Composable
internal fun WrapAndroidPin(activity: MainActivity, isPinSetup: Boolean, onBack: () -> Unit) {
    val pinViewModel by activity.viewModels<PinViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()

    BackHandler(true) {
        // Stop acting on back press
    }

    if (isPinSetup) {
        val onPinSelected:(String) -> Unit = {
            pinViewModel.savePin(it)
            onBack()
        }
        SetUpPin(onBack = onBack, onPinSelected = onPinSelected)
    } else {
        val lastPin = pinViewModel.lastSavedPin.collectAsStateWithLifecycle().value
        val isTouchIdOrFaceIdEnabled = pinViewModel.isTouchIdOrFaceIdEnabled.collectAsStateWithLifecycle().value
        if (lastPin == null || isTouchIdOrFaceIdEnabled == null) {
            // we can show Loader if it take time, this value should not be null in real case
        } else {
            AuthenticatePin(
                lastPin = lastPin,
                onBack = onBack,
                onAuthentication = walletViewModel::updateAuthenticationState
            )

            LaunchedEffect(key1 = Unit) {
                if (isTouchIdOrFaceIdEnabled && pinViewModel.isBioMetricEnabledOnMobile()) {
                    activity.authenticate("", activity.getString(R.string.biometric_backup_phrase_title)) {
                        walletViewModel.updateAuthenticationState(true)
                    }
                }
            }
        }
    }
}
