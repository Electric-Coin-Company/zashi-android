package co.electriccoin.zcash.ui.design.compat

import android.content.Context
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import co.electriccoin.zcash.spackle.AndroidApiVersion
import co.electriccoin.zcash.ui.design.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FontCompat {
    fun isFontPrefetchNeeded() = !AndroidApiVersion.isAtLeastO

    suspend fun prefetchFontsLegacy(context: Context) {
        prefetchFontLegacy(context, R.font.rubik_medium)
        prefetchFontLegacy(context, R.font.rubik_regular)
    }

    /**
     * Pre-fetches fonts on Android N (API 25) and below.
     */
    /*
     * ResourcesCompat is used implicitly by Compose on older Android versions.
     * The backwards compatibility library performs disk IO and then
     * caches the results.  This moves that IO off the main thread, to prevent ANRs and
     * jank during app startup.
     */
    private suspend fun prefetchFontLegacy(context: Context, @FontRes fontRes: Int) =
        withContext(Dispatchers.IO) {
            ResourcesCompat.getFont(context, fontRes)
        }
}
