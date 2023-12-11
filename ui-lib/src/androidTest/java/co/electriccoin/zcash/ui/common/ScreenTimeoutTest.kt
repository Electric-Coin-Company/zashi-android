package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ScreenTimeoutTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun acquireAndReleaseScreenTimeout() =
        runTest {
            val testSetup = TestSetup(composeTestRule)

            assertEquals(1, testSetup.getScreenTimeoutCount())

            testSetup.mutableScreenTimeoutFlag.update { false }
            composeTestRule.awaitIdle()
            assertEquals(0, testSetup.getScreenTimeoutCount())
        }

    private class TestSetup(composeTestRule: ComposeContentTestRule) {
        val mutableScreenTimeoutFlag = MutableStateFlow(true)

        private val screenTimeout = ScreenTimeout()

        fun getScreenTimeoutCount() = screenTimeout.referenceCount.value

        init {
            composeTestRule.setContent {
                CompositionLocalProvider(LocalScreenTimeout provides screenTimeout) {
                    ZcashTheme {
                        val disableScreenTimeout by mutableScreenTimeoutFlag.collectAsState()

                        TestView(disableScreenTimeout)
                    }
                }
            }
        }

        @Composable
        private fun TestView(disableScreenTimeout: Boolean) {
            if (disableScreenTimeout) {
                DisableScreenTimeout()
            }
        }
    }
}
