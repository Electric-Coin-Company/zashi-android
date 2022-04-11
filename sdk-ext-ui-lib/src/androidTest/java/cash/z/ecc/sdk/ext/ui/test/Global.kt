package cash.z.ecc.sdk.ext.ui.test

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import java.util.Locale

fun getStringResource(@StringRes resId: Int) = ApplicationProvider.getApplicationContext<Context>().getString(resId)

fun getStringResourceWithArgs(@StringRes resId: Int, formatArgs: Array<Any>) = ApplicationProvider.getApplicationContext<Context>().getString(resId, *formatArgs)

fun isLocaleRTL(locale: Locale) = TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL
