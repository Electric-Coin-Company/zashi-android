package co.electriccoin.zcash.ui.screen.update_available.view

import android.app.Activity
import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update_available.TestUpdateAvailableActivity
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import com.google.android.play.core.install.model.ActivityResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AppUpdateCheckerImpTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestUpdateAvailableActivity>()

    companion object {
        val context: Context = ApplicationProvider.getApplicationContext()
        val updateInfo = UpdateInfoFixture.new(appUpdateInfo = null)
        val updateChecker = AppUpdateCheckerImp.new()
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun check_for_update_availability_test() = runTest {
        assertNotNull(updateInfo)
        assertNotNull(updateChecker)

        updateChecker.checkForUpdateAvailability(
            context,
            3
        ).onFirst {
            assertTrue(it.state == UpdateState.Failed || it.state == UpdateState.Prepared)
        }
    }

    @Test
    @MediumTest
    fun fixture_copy_test() {
        val copied = updateInfo.copy(state = UpdateState.Running)
        assertNotNull(copied)
        assertNotEquals(updateInfo.state, copied.state)
        assertEquals(UpdateState.Running, copied.state)
        assertEquals(updateInfo.appUpdateInfo, copied.appUpdateInfo)
    }

    @Test
    @MediumTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun start_update_availability_test() = runTest {
        val copied = updateInfo.copy(appUpdateInfo = UpdateInfoFixture.APP_UPDATE_INFO)
        assertNotNull(copied.appUpdateInfo)

        updateChecker.startUpdate(
            composeTestRule.activity,
            copied.appUpdateInfo
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
