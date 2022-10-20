package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.android.sdk.ext.collectWith
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class ButtonTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    @Test
    @MediumTest
    fun timedButtonTest(): Unit = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testSetup = newTestSetup(testDispatcher, 2.seconds)
        val mark = testTimeSource.markNow()

        launch(Dispatchers.Main) {
            testSetup.interactionSource.emit(PressInteraction.Press(Offset.Zero))
            advanceTimeBy(3.seconds.inWholeMilliseconds)
        }

        launch {
            testSetup.mutableActionExecuted.collectWith(this) {
                if (!it) return@collectWith

                assertTrue { mark.elapsedNow() >= 2.seconds }
                this.cancel()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @MediumTest
    fun buttonClickTest() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testSetup = newTestSetup(testDispatcher, 2.seconds)

        composeTestRule.onNodeWithText("button").also {
            it.performClick()
        }
        advanceTimeBy(3.seconds.inWholeMilliseconds)
        assertFalse { testSetup.mutableActionExecuted.value }
    }

    private fun newTestSetup(testDispatcher: CoroutineDispatcher, duration: Duration) = TestSetup(testDispatcher, composeTestRule, duration)

    private class TestSetup(coroutineDispatcher: CoroutineDispatcher, composeTestRule: ComposeContentTestRule, duration: Duration) {
        val mutableActionExecuted = MutableStateFlow(false)
        val interactionSource = MutableInteractionSource()

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    TimedButton(
                        duration = duration,
                        onClick = { mutableActionExecuted.update { true } },
                        coroutineDispatcher = coroutineDispatcher,
                        content = { Text(text = "button") },
                        interactionSource = interactionSource
                    )
                }
            }
        }
    }
}
