package co.electriccoin.zcash.ui.screen.update_available.integration

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.viewModelScope
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.onFirst
import co.electriccoin.zcash.ui.screen.update_available.TestUpdateAvailableActivity
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import co.electriccoin.zcash.ui.screen.update_available.view.AppUpdateCheckerMock
import co.electriccoin.zcash.ui.screen.update_available.viewmodel.UpdateAvailableViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class UpdateAvailableViewModelTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestUpdateAvailableActivity>()

    private lateinit var viewModel: UpdateAvailableViewModel
    private lateinit var checker: AppUpdateCheckerMock
    private lateinit var updateInfo: UpdateInfo

    @Before
    fun setup() {
        checker = AppUpdateCheckerMock.new()

        updateInfo = UpdateInfoFixture.new(appUpdateInfo = UpdateInfoFixture.APP_UPDATE_INFO)

        viewModel = UpdateAvailableViewModel(
            composeTestRule.activity.application,
            updateInfo,
            checker
        )
    }

    @After
    fun cleanup() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    @MediumTest
    fun validate_result_of_check_for_app_update() = runTest {
        viewModel.updateInfo.onFirst { newInfo ->
            assertEquals(newInfo.priority, AppUpdateCheckerMock.resultUpdateInfo.priority)
            assertEquals(newInfo.state, AppUpdateCheckerMock.resultUpdateInfo.state)
        }

        viewModel.checkForAppUpdate(composeTestRule.activity)
    }

    @Test
    @MediumTest
    fun validate_result_of_go_for_update() = runTest {
        // vm emits Running and then one of the result states
        viewModel.goForUpdate(composeTestRule.activity, updateInfo.appUpdateInfo)

        viewModel.updateInfo.take(2).collectIndexed { index, newInfo ->
            if (index == 0)
                assertEquals(UpdateState.Running, newInfo.state)
            else
                assertContains(
                    listOf(UpdateState.Done, UpdateState.Canceled, UpdateState.Failed),
                    newInfo.state
                )
        }
    }
}
