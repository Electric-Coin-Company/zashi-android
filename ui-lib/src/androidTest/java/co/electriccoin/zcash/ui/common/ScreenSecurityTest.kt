package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScreenSecurityTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @SmallTest
    fun sanity_is_running_test_check() {
        assertTrue(
            actual = isRunningTest,
            message = "isRunningTest must always be TRUE while running from an automated Android UI test."
        )
    }

    @Test
    @SmallTest
    fun sanity_should_secure_screen_check() {
        assertTrue(
            actual = shouldSecureScreen,
            message = "shouldSecureScreen must always be TRUE while running from an automated Android UI test."
        )
    }

    @Test
    @MediumTest
    fun acquireAndReleaseScreenSecurity() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(1, testSetup.getSecureScreenCount())

        testSetup.mutableSecureScreenFlag.update { false }
        composeTestRule.awaitIdle()
        assertEquals(0, testSetup.getSecureScreenCount())
    }

    private class TestSetup(composeTestRule: ComposeContentTestRule) {

        val mutableSecureScreenFlag = MutableStateFlow(true)

        private val screenSecurity = ScreenSecurity()

        fun getSecureScreenCount() = screenSecurity.referenceCount.value

        init {
            runTest {
                composeTestRule.setContent {
                    CompositionLocalProvider(LocalScreenSecurity provides screenSecurity) {
                        ZcashTheme {
                            val secureScreen by mutableSecureScreenFlag.collectAsState()

                            TestView(secureScreen)
                        }
                    }
                }
            }
        }

        @Composable
        private fun TestView(secureScreen: Boolean) {
            if (secureScreen) {
                SecureScreen()
            }
        }
    }
}
