package co.electriccoin.zcash.ui.design.newcomponent

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import kotlin.annotation.AnnotationRetention.SOURCE

@Preview(name = "1: Light preview", showBackground = true)
@Preview(name = "2: Dark preview", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Retention(SOURCE)
annotation class PreviewScreens
