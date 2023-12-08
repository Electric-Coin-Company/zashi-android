package co.electriccoin.zcash.crash.android.internal.local

import android.content.Context
import android.os.Bundle
import co.electriccoin.zcash.crash.ReportableException
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal fun ReportableException.Companion.new(
    context: Context,
    throwable: Throwable,
    isUncaught: Boolean,
    clock: Clock = Clock.System
): ReportableException {
    val versionName =
        context.packageManager.getPackageInfoCompat(context.packageName, 0L).versionName
            ?: "null"

    return ReportableException(
        throwable.javaClass.name,
        throwable.stackTraceToString(),
        versionName,
        isUncaught,
        clock.now()
    )
}

internal fun ReportableException.toBundle() =
    Bundle().apply {
        // Although Exception is Serializable, some Kotlin Coroutines exception classes break this
        // API contract.  Therefore we have to convert to a string here.
        putSerializable(ReportableException.EXTRA_STRING_CLASS_NAME, exceptionClass)
        putSerializable(ReportableException.EXTRA_STRING_TRACE, exceptionTrace)
        putString(ReportableException.EXTRA_STRING_APP_VERSION, appVersion)
        putBoolean(ReportableException.EXTRA_BOOLEAN_IS_UNCAUGHT, isUncaught)
        putLong(ReportableException.EXTRA_LONG_WALLTIME_MILLIS, time.toEpochMilliseconds())
    }

internal fun ReportableException.Companion.fromBundle(bundle: Bundle): ReportableException {
    val className = bundle.getString(EXTRA_STRING_CLASS_NAME)!!
    val trace = bundle.getString(EXTRA_STRING_TRACE)!!
    val appVersion = bundle.getString(EXTRA_STRING_APP_VERSION)!!
    val isUncaught = bundle.getBoolean(EXTRA_BOOLEAN_IS_UNCAUGHT, false)
    val time = Instant.fromEpochMilliseconds(bundle.getLong(EXTRA_LONG_WALLTIME_MILLIS, 0))

    return ReportableException(className, trace, appVersion, isUncaught, time)
}

private val ReportableException.Companion.EXTRA_STRING_CLASS_NAME
    get() = "co.electriccoin.zcash.crash.extra.STRING_CLASS_NAME" // $NON-NLS-1$

private val ReportableException.Companion.EXTRA_STRING_TRACE
    get() = "co.electriccoin.zcash.crash.extra.STRING_TRACE" // $NON-NLS-1$

private val ReportableException.Companion.EXTRA_STRING_APP_VERSION: String
    get() = "co.electriccoin.zcash.crash.extra.STRING_APP_VERSION" // $NON-NLS-1$

private val ReportableException.Companion.EXTRA_BOOLEAN_IS_UNCAUGHT
    get() = "co.electriccoin.zcash.crash.extra.BOOLEAN_IS_UNCAUGHT" // $NON-NLS-1$

private val ReportableException.Companion.EXTRA_LONG_WALLTIME_MILLIS
    get() = "co.electriccoin.zcash.crash.extra.LONG_WALLTIME_MILLIS" // $NON-NLS-1$
