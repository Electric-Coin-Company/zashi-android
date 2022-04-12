package co.electriccoin.zcash.ui.test

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider

fun getStringResource(@StringRes resId: Int) = ApplicationProvider.getApplicationContext<Context>().getString(resId)

fun getStringResourceWithArgs(@StringRes resId: Int, vararg formatArgs: String) = ApplicationProvider.getApplicationContext<Context>().getString(resId, *formatArgs)
