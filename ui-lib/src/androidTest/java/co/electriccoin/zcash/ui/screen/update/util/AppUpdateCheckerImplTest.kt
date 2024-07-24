package co.electriccoin.zcash.ui.screen.update.util

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImpl
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.test.getAppContext
import com.google.android.play.core.install.model.ActivityResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppUpdateCheckerImplTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    companion object {
        val context: Context = getAppContext()
        val updateChecker = AppUpdateCheckerImpl()
    }

    private fun getAppUpdateInfoFlow(): Flow<UpdateInfo> {
        return updateChecker.newCheckForUpdateAvailabilityFlow(
            context
        )
    }

    @Test
    @MediumTest
    fun check_for_update_availability_test() =
        runTest {
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
    fun start_update_availability_test() =
        runTest {
            getAppUpdateInfoFlow().onFirst { updateInfo ->
                // In case we get result with FAILED state, e.g. app is still not released in the Google
                // Play store, there is no way to continue with the test.
                if (updateInfo.state == UpdateState.Failed) {
                    assertNull(updateInfo.appUpdateInfo)
                    return@onFirst
                }

                assertNotNull(updateInfo.appUpdateInfo)

                updateChecker.newStartUpdateFlow(
                    composeTestRule.activity,
                    updateInfo.appUpdateInfo!!
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
