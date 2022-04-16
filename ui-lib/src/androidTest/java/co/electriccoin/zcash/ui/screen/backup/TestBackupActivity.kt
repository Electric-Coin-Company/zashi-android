package co.electriccoin.zcash.ui.screen.backup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState

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
                    WrapBackup(
                        persistableWallet = PersistableWalletFixture.new(),
                        backupState = BackupState(BackupStage.EducationOverview),
                        onCopyToClipboard = {},
                        onBackupComplete = {}
                    )
                }
            }
        }
    }
}
