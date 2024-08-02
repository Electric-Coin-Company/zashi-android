package co.electriccoin.zcash.ui.design.newcomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            content()
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@ScreenPreview
@Composable
private fun BottomBarPreview() = ZcashTheme {
    BottomBar {
        PrimaryButton(
            state = ButtonState(text = stringRes("Save Button")),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
    }
}
