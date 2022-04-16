package co.electriccoin.zcash.ui.screen.backup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState
import co.electriccoin.zcash.ui.screen.backup.view.BackupWallet

class TestBackupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUiContent()
    }

    private fun setupUiContent() {
        setContent {
            ZcashTheme {
                GradientSurface(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    WrapBackup()
                }
            }
        }
    }

    @Composable
    private fun WrapBackup() {
        BackupWallet(
            PersistableWalletFixture.new(),
            BackupState(BackupStage.EducationOverview),
            TestChoicesFixture.new(mutableMapOf()),
            onCopyToClipboard = {},
            onComplete = {},
            onChoicesChanged = {}
        )
    }
}
