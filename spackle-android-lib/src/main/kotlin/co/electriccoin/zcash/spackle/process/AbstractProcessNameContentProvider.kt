package co.electriccoin.zcash.spackle.process

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import co.electriccoin.zcash.spackle.AndroidApiVersion

/**
 * Implement an empty subclass of this ContentProvider for each process the application uses.
 *
 * This works in conjunction with [ProcessNameCompat].
 */
open class AbstractProcessNameContentProvider : ContentProvider() {
    override fun onCreate() = true

    override fun attachInfo(
        context: Context,
        info: ProviderInfo
    ) {
        super.attachInfo(context, info)

        val processName: String =
            if (AndroidApiVersion.isAtLeastTiramisu) {
                getProcessNameTPlus()
            } else if (AndroidApiVersion.isAtLeastP) {
                getProcessNamePPlus()
            } else {
                getProcessNameLegacy(context, info)
            }

        ProcessNameCompat.setProcessName(processName)
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private fun getProcessNameTPlus() = Process.myProcessName()

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun getProcessNamePPlus(): String = Application.getProcessName()

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = throw UnsupportedOperationException()

    override fun getType(uri: Uri): String? = throw UnsupportedOperationException()

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? = throw UnsupportedOperationException()

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = throw UnsupportedOperationException()

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = throw UnsupportedOperationException()

    companion object {
        internal fun getProcessNameLegacy(
            context: Context,
            info: ProviderInfo
        ) = info.processName ?: context.applicationInfo.processName ?: context.packageName
    }
}
