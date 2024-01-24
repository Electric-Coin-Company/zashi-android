package co.electriccoin.zcash.ui.screen.send

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

class SendViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val initialState: SendStage,
    private val initialZecSend: ZecSend?,
    private val initialSendArgumentWrapper: SendArgumentsWrapper?,
    private val hasCameraFeature: Boolean
) {
    private val onBackCount = AtomicInteger(0)
    private val onSettingsCount = AtomicInteger(0)
    private val onCreateCount = AtomicInteger(0)
    private val onScannerCount = AtomicInteger(0)
    val mutableActionExecuted = MutableStateFlow(false)

    @Volatile
    private var lastSendStage: SendStage? = null

    @Volatile
    private var lastZecSend: ZecSend? = null

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    fun getOnCreateCount(): Int {
        composeTestRule.waitForIdle()
        return onCreateCount.get()
    }

    fun getOnScannerCount(): Int {
        composeTestRule.waitForIdle()
        return onScannerCount.get()
    }

    fun getLastZecSend(): ZecSend? {
        composeTestRule.waitForIdle()
        return lastZecSend
    }

    fun getLastSendStage(): SendStage? {
        composeTestRule.waitForIdle()
        return lastSendStage
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        val (sendStage, setSendStage) =
            rememberSaveable { mutableStateOf(initialState) }

        lastSendStage = sendStage

        val onBackAction = {
            onBackCount.incrementAndGet()
            when (sendStage) {
                SendStage.Form -> {}
                SendStage.Confirmation -> setSendStage(SendStage.Form)
                SendStage.Sending -> {}
                SendStage.SendFailure -> setSendStage(SendStage.Form)
                SendStage.SendSuccessful -> {}
            }
        }

        BackHandler {
            onBackAction()
        }

        val (zecSend, setZecSend) =
            rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(initialZecSend) }

        lastZecSend = zecSend

        ZcashTheme {
            Send(
                walletSnapshot =
                    WalletSnapshotFixture.new(
                        saplingBalance =
                            WalletBalance(
                                total = Zatoshi(Zatoshi.MAX_INCLUSIVE.div(100)),
                                available = Zatoshi(Zatoshi.MAX_INCLUSIVE.div(100))
                            )
                    ),
                sendStage = sendStage,
                sendArgumentsWrapper = initialSendArgumentWrapper,
                onSendStageChange = setSendStage,
                zecSend = zecSend,
                onZecSendChange = setZecSend,
                onBack = onBackAction,
                goBalances = {
                    // TODO [#1194]: Cover Current balances UI widget with tests
                    // TODO [#1194]: https://github.com/Electric-Coin-Company/zashi-android/issues/1194
                },
                onSettings = { onSettingsCount.incrementAndGet() },
                onCreateAndSend = {
                    onCreateCount.incrementAndGet()
                    lastZecSend = it
                    mutableActionExecuted.update { true }
                },
                onQrScannerOpen = {
                    onScannerCount.incrementAndGet()
                },
                hasCameraFeature = hasCameraFeature
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            DefaultContent()
        }
    }
}
