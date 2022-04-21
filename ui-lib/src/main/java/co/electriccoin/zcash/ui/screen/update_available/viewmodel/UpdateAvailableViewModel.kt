@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow

class UpdateAvailableViewModel(application: Application) : AndroidViewModel(application) {

    val updateInfo: MutableStateFlow<UpdateInfo> = MutableStateFlow(UpdateInfoFixture.new())

    fun goForUpdate() {
        // TODO temporary usage
        updateInfo.tryEmit(
            UpdateInfoFixture.new(
                0,
                true
            )
        )
    }

    fun skipUpdate() {
        // TODO temporary usage
        updateInfo.tryEmit(
            UpdateInfoFixture.new(
                1,
                false
            )
        )
    }
}
