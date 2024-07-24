package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CheckUpdateViewModel(
    application: Application,
    private val appUpdateChecker: AppUpdateChecker
) : AndroidViewModel(application) {
    val updateInfo: MutableStateFlow<UpdateInfo?> = MutableStateFlow(null)

    fun checkForAppUpdate() {
        viewModelScope.launch {
            appUpdateChecker.newCheckForUpdateAvailabilityFlow(
                getApplication()
            ).onFirst { newInfo ->
                updateInfo.value = newInfo
            }
        }
    }
}
