package cash.z.ecc.ui.screen.seed.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import cash.z.ecc.ui.R
import cash.z.ecc.ui.test.getStringResource
import cash.z.ecc.ui.theme.ZcashTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class SeedViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun back() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.seed_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun copyToClipboard() = runTest {
        val testSetup = TestSetup(composeTestRule)

        assertEquals(0, testSetup.getCopyToClipboardCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.seed_copy)).also {
            it.performScrollTo()
            it.performClick()
        }

        assertEquals(1, testSetup.getCopyToClipboardCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule) {

        private var onBackCount = AtomicInteger(0)
        private var onCopyToClipboardCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        fun getCopyToClipboardCount(): Int {
            composeTestRule.waitForIdle()
            return onCopyToClipboardCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    Seed(
                        PersistableWalletFixture.new(),
                        onBack = {
                            onBackCount.incrementAndGet()
                        },
                        onCopyToClipboard = {
                            onCopyToClipboardCount.incrementAndGet()
                        }
                    )
                }
            }
        }
    }
}
