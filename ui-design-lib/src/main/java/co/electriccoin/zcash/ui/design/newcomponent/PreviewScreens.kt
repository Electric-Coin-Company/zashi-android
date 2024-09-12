package co.electriccoin.zcash.ui.design.newcomponent

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import kotlin.annotation.AnnotationRetention.SOURCE

// TODO [#1580]: Suppress compilation warning on PreviewScreens
// https://github.com/Electric-Coin-Company/zashi-android/issues/1580
@Suppress("UnusedPrivateMember")
@Preview(name = "1: Light preview", showBackground = true)
@Preview(name = "2: Dark preview", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Retention(SOURCE)
annotation class PreviewScreens
