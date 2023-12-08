package co.electriccoin.zcash.ui.test

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider

fun getAppContext(): Context = ApplicationProvider.getApplicationContext()

fun getStringResource(
    @StringRes resId: Int
) = getAppContext().getString(resId)

fun getStringResourceWithArgs(
    @StringRes resId: Int,
    vararg formatArgs: String
) = getAppContext().getString(resId, *formatArgs)
