package co.electriccoin.zcash.ui.screen.exportdata.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import java.io.File

object FileShareUtil {
    const val SHARE_OUTSIDE_THE_APP_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK

    const val SHARE_CONTENT_PERMISSION_FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION

    const val ZASHI_INTERNAL_DATA_MIME_TYPE = "application/octet-stream" // NON-NLS

    const val ZASHI_INTERNAL_DATA_AUTHORITY = "co.electriccoin.zcash.provider" // NON-NLS
    const val ZASHI_INTERNAL_DATA_AUTHORITY_DEBUG = "co.electriccoin.zcash.debug.provider" // NON-NLS

    const val ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET = "co.electriccoin.zcash.provider.testnet" // NON-NLS
    const val ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET_DEBUG = "co.electriccoin.zcash.debug.provider.testnet" // NON-NLS

    /**
     * Returns a new share internal app data intent with necessary permission granted exclusively to the data file.
     *
     * @param dataFilePath The private data file path we want to share
     *
     * @return Intent for launching an app for sharing
     */
    internal fun newShareContentIntent(
        context: Context,
        dataFilePath: String,
        versionInfo: VersionInfo
    ): Intent {
        val fileUri =
            FileProvider.getUriForFile(
                context,
                getAuthorityByVersionInfo(versionInfo),
                File(dataFilePath)
            )

        val dataIntent: Intent =
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = ZASHI_INTERNAL_DATA_MIME_TYPE
            }

        val shareDataIntent =
            Intent.createChooser(
                dataIntent,
                context.getString(R.string.export_data_export_data_chooser_title)
            ).apply {
                addFlags(
                    SHARE_CONTENT_PERMISSION_FLAGS or
                        SHARE_OUTSIDE_THE_APP_FLAGS
                )
            }

        return shareDataIntent
    }

    private fun getAuthorityByVersionInfo(versionInfo: VersionInfo) =
        if (versionInfo.isTestnet) {
            if (versionInfo.isDebuggable) {
                ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET_DEBUG
            } else {
                ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET
            }
        } else {
            if (versionInfo.isDebuggable) {
                ZASHI_INTERNAL_DATA_AUTHORITY_DEBUG
            } else {
                ZASHI_INTERNAL_DATA_AUTHORITY
            }
        }
}
