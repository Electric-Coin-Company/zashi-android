package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        shadowElevation = 4.dp,
        color = ZashiColors.Surfaces.bgPrimary
    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            content()
            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@PreviewScreens
@Composable
private fun BottomBarPreview() =
    ZcashTheme {
        ZashiBottomBar {
            ZashiButton(
                state = ButtonState(text = stringRes("Save Button")),
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
            )
        }
    }
