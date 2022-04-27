@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.cancel
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
    ): Flow<AppUpdateInfo?> = callbackFlow {
        delay(2000L)
        // AppUpdateInfo object from Play Core lib, unfortunately can not be easily instantiated,
        // so we need to pass null.
        trySend(null)
        awaitClose { cancel() }
    }

    override fun startUpdate(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo?,
        onUpdateResult: (resultCode: Int) -> Unit
    ) {
        activity.lifecycleScope.launch {
            delay(3000L)
            onUpdateResult(Activity.RESULT_OK)
        }
    }
}
