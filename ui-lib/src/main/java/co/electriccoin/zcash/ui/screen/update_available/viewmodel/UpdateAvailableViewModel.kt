@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.viewmodel

import android.app.Activity
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.ActivityResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UpdateAvailableViewModel(
    application: Application,
    updateInfo: UpdateInfo,
    private val appUpdateChecker: AppUpdateChecker
) : AndroidViewModel(application) {

    val updateInfo: MutableStateFlow<UpdateInfo> = MutableStateFlow(updateInfo)

    fun checkForAppUpdate() {
        viewModelScope.launch {
            appUpdateChecker.checkForUpdateAvailability(
                getApplication()
            ).onFirst { newInfo ->
                updateInfo.value = newInfo
            }
        }
    }

    fun goForUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo
    ) {
        updateInfo.value = updateInfo.value.copy(state = UpdateState.Running)

        viewModelScope.launch {
            appUpdateChecker.startUpdate(
                activity,
                appUpdateInfo
            ).onFirst { resultCode ->
                val state = when (resultCode) {
                    Activity.RESULT_OK -> UpdateState.Done
                    Activity.RESULT_CANCELED -> UpdateState.Canceled
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> UpdateState.Failed
                    else -> UpdateState.Prepared
                }
                updateInfo.value = updateInfo.value.copy(state = state)
            }
        }
    }

    fun remindLater() {
        // for mvp we just return user back to the previous screen
        updateInfo.value = updateInfo.value.copy(state = UpdateState.Canceled)
    }

    @Suppress("UNCHECKED_CAST")
    class UpdateAvailableViewModelFactory(
        private val application: Application,
        private val updateInfo: UpdateInfo,
        private val appUpdateChecker: AppUpdateChecker
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(UpdateAvailableViewModel::class.java)) {
                UpdateAvailableViewModel(application, updateInfo, appUpdateChecker) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
