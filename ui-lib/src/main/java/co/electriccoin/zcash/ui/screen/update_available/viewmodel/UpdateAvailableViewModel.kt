@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.viewmodel

import android.app.Activity
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateStage
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.ActivityResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class UpdateAvailableViewModel(
    application: Application,
    appUpdateInfo: AppUpdateInfo?,
    private val appUpdateChecker: AppUpdateChecker
) : AndroidViewModel(application) {

    val updateInfo: MutableStateFlow<UpdateInfo> = MutableStateFlow(
        UpdateInfoFixture.new(
            appUpdateChecker.getPriority(appUpdateInfo?.updatePriority() ?: 0),
            appUpdateChecker.isHighPriority(appUpdateInfo?.updatePriority() ?: 0),
            appUpdateInfo,
            UpdateStage.Failed
        )
    )

    fun checkForAppUpdate(activity: ComponentActivity) {
        activity.lifecycleScope.launch {
            appUpdateChecker.checkForUpdateAvailability(
                activity,
                appUpdateChecker.stanelessDays
            ).onFirst { newInfo ->
                updateInfo.value = updateInfo.updateAndGet {
                    it.apply { it.appUpdateInfo = newInfo }
                }
            }
        }
    }

    fun goForUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?
    ) {
        updateInfo.value = updateInfo.updateAndGet {
            it.apply { it.stage = UpdateStage.Running }
        }

        appUpdateChecker.startUpdate(
            activity,
            appUpdateInfo,
            onUpdateResult = { resultCode ->
                updateInfo.value = updateInfo.updateAndGet {
                    it.apply {
                        this.stage = when (resultCode) {
                            Activity.RESULT_OK -> UpdateStage.Done
                            Activity.RESULT_CANCELED -> UpdateStage.Canceled
                            ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> UpdateStage.Failed
                            else -> UpdateStage.Prepared
                        }
                    }
                }
            }
        )
    }

    fun remindLater() {
        // for mvp we just return user back to the previous screen
        updateInfo.value = updateInfo.updateAndGet {
            it.apply { it.stage = UpdateStage.Canceled }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class UpdateAvailableViewModelFactory(
        private val application: Application,
        private val appUpdateInfo: AppUpdateInfo?,
        private val appUpdateChecker: AppUpdateChecker
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(UpdateAvailableViewModel::class.java)) {
                UpdateAvailableViewModel(application, appUpdateInfo, appUpdateChecker) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
