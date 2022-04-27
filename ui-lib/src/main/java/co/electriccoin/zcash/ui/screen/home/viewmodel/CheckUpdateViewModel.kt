@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.home.viewmodel

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.flow.Flow

class CheckUpdateViewModel(
    application: Application,
    private val appUpdateChecker: AppUpdateChecker
) : AndroidViewModel(application) {

    fun checkForAppUpdate(activity: ComponentActivity): Flow<AppUpdateInfo?> {
        return appUpdateChecker.checkForUpdateAvailability(
            activity,
            appUpdateChecker.stanelessDays
        )
    }

    @Suppress("UNCHECKED_CAST")
    class CheckUpdateViewModelFactory(
        private val application: Application,
        private val appUpdateChecker: AppUpdateChecker
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(CheckUpdateViewModel::class.java)) {
                CheckUpdateViewModel(application, appUpdateChecker) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
