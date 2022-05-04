@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.view

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Suppress("MagicNumber")
class AppUpdateCheckerMock : AppUpdateChecker {

    companion object {
        fun new() = AppUpdateCheckerMock()

        val resultUpdateInfo = UpdateInfoFixture.new(
            appUpdateInfo = UpdateInfoFixture.APP_UPDATE_INFO,
            state = UpdateState.Prepared
        )
    }

    override val stalenessDays = 3

    override fun checkForUpdateAvailability(
        context: Context,
        stalenessDays: Int
    ): Flow<UpdateInfo> = callbackFlow {
        // delay(1000)
        trySend(resultUpdateInfo)
        awaitClose {}
    }

    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?
    ): Flow<Int> = callbackFlow {
        // delay(3000)
        trySend(Activity.RESULT_OK)
        awaitClose {}
    }
}
