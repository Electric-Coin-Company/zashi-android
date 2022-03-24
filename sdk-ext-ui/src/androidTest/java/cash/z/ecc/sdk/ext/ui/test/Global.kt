package cash.z.ecc.sdk.ext.ui.test

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider

fun getStringResource(@StringRes resId: Int) = ApplicationProvider.getApplicationContext<Context>().getString(resId)

fun getStringResourceWithArgs(@StringRes resId: Int, formatArgs: Array<Any>) = ApplicationProvider.getApplicationContext<Context>().getString(resId, *formatArgs)
