package co.electriccoin.zcash.ui.screen.update_available.view

import android.app.Activity
import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update_available.TestUpdateAvailableActivity
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import com.google.android.play.core.install.model.ActivityResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppUpdateCheckerImpTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestUpdateAvailableActivity>()

    companion object {
        val context: Context = ApplicationProvider.getApplicationContext()
        val updateChecker = AppUpdateCheckerImp.new()
    }

    private fun getAppUpdateInfoFlow(): Flow<UpdateInfo> {
        @Suppress("MagicNumber")
        return updateChecker.checkForUpdateAvailability(
            context,
            3
        )
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun check_for_update_availability_test() = runTest {
        assertNotNull(updateChecker)

        getAppUpdateInfoFlow().onFirst { updateInfo ->
            assertTrue(
                listOf(
                    UpdateState.Failed,
                    UpdateState.Prepared,
                    UpdateState.Done
                ).contains(updateInfo.state)
            )
        }
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun start_update_availability_test() = runTest {

        getAppUpdateInfoFlow().onFirst { updateInfo ->
            // In case we get result with FAILED state, e.g. app is still not released in the Google
            // Play store, there is no way to continue with the test.
            if (updateInfo.state == UpdateState.Failed) {
                assertNull(updateInfo.appUpdateInfo)
                return@onFirst
            }

            updateChecker.startUpdate(
                composeTestRule.activity,
                updateInfo.appUpdateInfo
            ).onFirst { result ->
                assertTrue {
                    listOf(
                        Activity.RESULT_OK,
                        Activity.RESULT_CANCELED,
                        ActivityResult.RESULT_IN_APP_UPDATE_FAILED
                    ).contains(result)
                }
            }
        }
    }
}
