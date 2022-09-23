package co.electriccoin.zcash.ui.common

import android.util.Log
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ScreenSecurityTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mutableSecureScreenFlag = MutableStateFlow(true)

    @Test
    @MediumTest
    fun acquireAndReleaseScreenSecurity() = runTest {
        val testSetup = TestSetup(composeTestRule, mutableSecureScreenFlag)

        assertEquals(1, testSetup.getSecureScreenCount())

        mutableSecureScreenFlag.update { false }
        composeTestRule.awaitIdle()
        assertEquals(0, testSetup.getSecureScreenCount())
    }

    private class TestSetup(composeTestRule: ComposeContentTestRule, private val mutableSecureScreenFlag: StateFlow<Boolean>) {

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
            } else {
                Log.e("detached", "")
            }
        }
    }
}
