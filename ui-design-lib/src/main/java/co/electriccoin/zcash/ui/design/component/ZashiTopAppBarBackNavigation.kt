package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun ZashiTopAppBarBackNavigation(
    backContentDescriptionText: String = stringResource(R.string.back_navigation_content_description),
    painter: Painter =
        painterResource(
            R.drawable.ic_zashi_navigation_back orDark R.drawable.ic_zashi_navigation_back_dark
        ),
    onBack: () -> Unit
) {
    Row {
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onBack) {
            Icon(
                painter = painter,
                contentDescription = backContentDescriptionText,
            )
        }
    }
}
