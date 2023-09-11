package co.electriccoin.zcash.ui.screen.about

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo
import co.electriccoin.zcash.ui.screen.about.view.About
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class AboutViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun setup() {
        newTestSetup()

        composeTestRule.onNodeWithText(VersionInfoFixture.VERSION_NAME, substring = true).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(gitSha, substring = true).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun back() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.onNodeWithContentDescription(getStringResource(R.string.about_back_content_description)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBackCount())
    }

    private fun newTestSetup() = TestSetup(
        composeTestRule,
        VersionInfoFixture.new(),
        // ConfigInfoFixture.new()
    )

    private class TestSetup(
        private val composeTestRule: ComposeContentTestRule,
        versionInfo: VersionInfo,
        // configInfo: ConfigInfo
    ) {

        private val onBackCount = AtomicInteger(0)

        fun getOnBackCount(): Int {
            composeTestRule.waitForIdle()
            return onBackCount.get()
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    About(versionInfo = versionInfo) {
                        onBackCount.incrementAndGet()
                    }
                }
            }
        }
    }
}
