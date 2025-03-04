package co.electriccoin.zcash.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.design.component.BrightenScreen
import co.electriccoin.zcash.ui.design.component.LocalScreenBrightness
import co.electriccoin.zcash.ui.design.component.ScreenBrightness
import co.electriccoin.zcash.ui.design.component.ScreenBrightnessState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ScreenBrightnessTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun acquireAndReleaseScreenBrightness() =
        runTest {
            val testSetup = TestSetup(composeTestRule)

            assertEquals(ScreenBrightnessState.FULL, testSetup.getSecureBrightnessCount())

            testSetup.mutableScreenBrightnessFlag.update { false }
            composeTestRule.awaitIdle()
            assertEquals(ScreenBrightnessState.NORMAL, testSetup.getSecureBrightnessCount())
        }

    private class TestSetup(composeTestRule: ComposeContentTestRule) {
        val mutableScreenBrightnessFlag = MutableStateFlow(true)

        private val screenBrightness = ScreenBrightness

        fun getSecureBrightnessCount() = screenBrightness.referenceSwitch.value

        init {
            runTest {
                composeTestRule.setContent {
                    CompositionLocalProvider(LocalScreenBrightness provides screenBrightness) {
                        ZcashTheme {
                            val secureScreen by mutableScreenBrightnessFlag.collectAsState()

                            TestView(secureScreen)
                        }
                    }
                }
            }
        }

        @Composable
        private fun TestView(brightenScreen: Boolean) {
            if (brightenScreen) {
                BrightenScreen()
            }
        }
    }
}
