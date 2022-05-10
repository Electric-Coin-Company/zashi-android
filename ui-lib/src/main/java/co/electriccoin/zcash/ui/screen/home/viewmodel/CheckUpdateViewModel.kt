package co.electriccoin.zcash.ui.screen.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CheckUpdateViewModel(
    application: Application,
    private val appUpdateChecker: AppUpdateChecker
) : AndroidViewModel(application) {

    val updateInfo: MutableStateFlow<UpdateInfo?> = MutableStateFlow(null)

    fun checkForAppUpdate() {
        viewModelScope.launch {
            appUpdateChecker.checkForUpdateAvailability(
                getApplication(),
                appUpdateChecker.stalenessDays
            ).onFirst { newInfo ->
                updateInfo.value = newInfo
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class CheckUpdateViewModelFactory(
        private val application: Application,
        private val appUpdateChecker: AppUpdateChecker
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(CheckUpdateViewModel::class.java)) {
                CheckUpdateViewModel(application, appUpdateChecker) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
