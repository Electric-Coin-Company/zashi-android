package co.electriccoin.zcash.ui.screen.update.viewmodel

import android.app.Activity
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.ActivityResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateViewModel(
    application: Application,
    updateInfo: UpdateInfo,
    private val appUpdateChecker: AppUpdateChecker
) : AndroidViewModel(application) {
    val updateInfo: MutableStateFlow<UpdateInfo> = MutableStateFlow(updateInfo)

    fun checkForAppUpdate() {
        viewModelScope.launch {
            appUpdateChecker.newCheckForUpdateAvailabilityFlow(
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
        if (updateInfo.value.state == UpdateState.Running) {
            return
        }

        updateInfo.value = updateInfo.value.copy(state = UpdateState.Running)

        viewModelScope.launch {
            appUpdateChecker.newStartUpdateFlow(
                activity,
                appUpdateInfo
            ).onFirst { resultCode ->
                val state =
                    when (resultCode) {
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
        updateInfo.update { it.copy(state = UpdateState.Canceled) }
    }
}
