package co.electriccoin.zcash.ui.screen.request.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.request.model.RequestState

@Composable
internal fun RequestQrCodeView(
    state: RequestState.QrCode,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        Text(text = "QrCodeShare")
    }
}