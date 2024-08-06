package co.electriccoin.zcash.ui.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "1: Light preview", showBackground = true)
@Preview(name = "2: Dark preview", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewScreens
