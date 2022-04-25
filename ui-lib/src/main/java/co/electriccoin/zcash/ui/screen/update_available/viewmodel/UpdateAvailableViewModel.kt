@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.electriccoin.zcash.ui.AppUpdateCheckerImpl
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow

class UpdateAvailableViewModel(
    application: Application,
    appUpdateInfo: AppUpdateInfo
) : AndroidViewModel(application) {

    val updateInfo: MutableStateFlow<UpdateInfo> = MutableStateFlow(
        UpdateInfoFixture.new(
            AppUpdateCheckerImpl.getPriority(appUpdateInfo.updatePriority()),
            AppUpdateCheckerImpl.isHighPriority(appUpdateInfo.updatePriority()),
            appUpdateInfo
        )
    )

    fun goForUpdate() {
        TODO()
    }

    fun skipUpdate() {
        TODO()
    }

    @Suppress("UNCHECKED_CAST")
    class UpdateAvailableViewModelFactory(
        private val application: Application,
        private val appUpdateInfo: AppUpdateInfo
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(UpdateAvailableViewModel::class.java)) {
                UpdateAvailableViewModel(application, appUpdateInfo) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
