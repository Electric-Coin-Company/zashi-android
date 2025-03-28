package co.electriccoin.zcash.ui.screen.restore.view

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.model.ZcashNetwork
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.compose.LocalScreenSecurity
import co.electriccoin.zcash.ui.common.compose.ScreenSecurity
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restore.state.RestoreState
import co.electriccoin.zcash.ui.screen.restore.state.WordList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RestoreViewSecuredScreenTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun acquireScreenSecurity() =
        runTest {
            val testSetup = TestSetup(composeTestRule)

            assertEquals(1, testSetup.getSecureScreenCount())
        }

    private class TestSetup(
        composeTestRule: ComposeContentTestRule
    ) {
        private val screenSecurity = ScreenSecurity()

        fun getSecureScreenCount() = screenSecurity.referenceCount.value

        init {
            composeTestRule.setContent {
                CompositionLocalProvider(LocalScreenSecurity provides screenSecurity) {
                    ZcashTheme {
                        RestoreWallet(
                            ZcashNetwork.Mainnet,
                            RestoreState(),
                            Mnemonics.getCachedWords(Locale.ENGLISH.language).toPersistentSet(),
                            WordList(emptyList()),
                            restoreHeight = null,
                            setRestoreHeight = {},
                            onBack = { },
                            paste = { "" },
                            onFinish = { }
                        )
                    }
                }
            }
        }
    }
}
