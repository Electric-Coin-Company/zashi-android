package co.electriccoin.zcash.ui.integration.test.screen.update.viewmodel

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.viewModelScope
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.integration.test.common.IntegrationTestingActivity
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerMock
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.screen.update.viewmodel.UpdateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UpdateViewModelTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<IntegrationTestingActivity>()

    private lateinit var viewModel: UpdateViewModel
    private lateinit var checker: AppUpdateCheckerMock
    private lateinit var initialUpdateInfo: UpdateInfo

    @Before
    fun setup() {
        checker = AppUpdateCheckerMock()

        initialUpdateInfo =
            UpdateInfoFixture.new(
                appUpdateInfo = null,
                state = UpdateState.Prepared,
                priority = AppUpdateChecker.Priority.LOW,
                force = false
            )

        viewModel =
            UpdateViewModel(
                composeTestRule.activity.application,
                initialUpdateInfo,
                checker
            )
    }

    @After
    fun cleanup() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    @MediumTest
    fun validate_result_of_update_methods_calls() =
        runTest {
            viewModel.checkForAppUpdate()

            // Although this test does not copy the real world situation, as the initial and result objects
            // should be mostly the same, we test VM proper functionality. VM emits the initial object
            // defined in this class, then we expect the result object from the AppUpdateCheckerMock class
            // and a newly acquired AppUpdateInfo object.
            viewModel.updateInfo.take(4).collectIndexed { index, incomingInfo ->
                when (index) {
                    0 -> {
                        // checkForAppUpdate initial callback
                        incomingInfo.also {
                            assertNull(it.appUpdateInfo)

                            assertEquals(initialUpdateInfo.state, it.state)
                            assertEquals(initialUpdateInfo.appUpdateInfo, it.appUpdateInfo)
                            assertEquals(initialUpdateInfo.priority, it.priority)
                            assertEquals(initialUpdateInfo.state, it.state)
                            assertEquals(initialUpdateInfo.isForce, it.isForce)
                        }
                    }
                    1 -> {
                        // checkForAppUpdate result callback
                        incomingInfo.also {
                            assertNotNull(it.appUpdateInfo)

                            assertEquals(AppUpdateCheckerMock.resultUpdateInfo.state, it.state)
                            assertEquals(AppUpdateCheckerMock.resultUpdateInfo.priority, it.priority)
                            assertEquals(AppUpdateCheckerMock.resultUpdateInfo.isForce, it.isForce)
                        }

                        // now we can start the update
                        viewModel.goForUpdate(composeTestRule.activity, incomingInfo.appUpdateInfo!!)
                    }
                    2 -> {
                        // goForUpdate initial callback
                        assertNotNull(incomingInfo.appUpdateInfo)
                        assertEquals(UpdateState.Running, incomingInfo.state)
                    }
                    3 -> {
                        // goForUpdate result callback
                        assertNotNull(incomingInfo.appUpdateInfo)
                        assertEquals(UpdateState.Done, incomingInfo.state)
                    }
                }
            }
        }
}
