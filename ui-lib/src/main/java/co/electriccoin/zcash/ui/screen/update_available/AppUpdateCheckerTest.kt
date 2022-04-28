@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@Suppress("MagicNumber")
class AppUpdateCheckerTest : AppUpdateChecker {

    companion object {
        fun new() = AppUpdateCheckerTest()
    }

    override val stanelessDays = 3

    override fun checkForUpdateAvailability(
        context: Context,
        stalenessDays: Int
    ): Flow<UpdateInfo> = callbackFlow {
        delay(1000L)
        trySend(
            UpdateInfoFixture.new(
                // just for test purposes we use very simple AppUpdateInfo object instance
                appUpdateInfo = AppUpdateInfo.zzb(
                    "",
                    -1,
                    UpdateAvailability.UPDATE_AVAILABLE,
                    InstallStatus.UNKNOWN,
                    1,
                    1,
                    1,
                    1,
                    1,
                    1,
                    null,
                    null,
                    null,
                    null
                )
            )
        )
        awaitClose {}
    }

    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?,
        onUpdateResult: (resultCode: Int) -> Unit
    ) {
        activity.lifecycleScope.launch {
            delay(1000L)
            onUpdateResult(Activity.RESULT_OK)
        }
    }
}
