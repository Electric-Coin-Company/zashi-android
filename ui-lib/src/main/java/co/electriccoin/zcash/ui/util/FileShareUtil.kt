package co.electriccoin.zcash.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import co.electriccoin.zcash.ui.common.model.DistributionDimension
import co.electriccoin.zcash.ui.common.model.VersionInfo
import java.io.File

object FileShareUtil {
    const val SHARE_OUTSIDE_THE_APP_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK

    const val SHARE_CONTENT_PERMISSION_FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION

    const val ZASHI_INTERNAL_DATA_MIME_TYPE = "application/octet-stream" // NON-NLS
    const val ZASHI_QR_CODE_MIME_TYPE = "image/png" // NON-NLS

    const val ZASHI_INTERNAL_DATA_AUTHORITY = "co.electriccoin.zcash.provider" // NON-NLS
    const val ZASHI_INTERNAL_DATA_AUTHORITY_DEBUG = "co.electriccoin.zcash.debug.provider" // NON-NLS

    const val ZASHI_INTERNAL_DATA_FOSS_AUTHORITY = "co.electriccoin.zcash.foss.provider" // NON-NLS
    const val ZASHI_INTERNAL_DATA_FOSS_AUTHORITY_DEBUG = "co.electriccoin.zcash.foss.debug.provider" // NON-NLS

    const val ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET = "co.electriccoin.zcash.provider.testnet" // NON-NLS
    const val ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET_DEBUG = "co.electriccoin.zcash.debug.provider.testnet" // NON-NLS

    const val ZASHI_INTERNAL_DATA_FOSS_AUTHORITY_TESTNET = "co.electriccoin.zcash.foss.provider.testnet" // NON-NLS

    // NON-NLS
    const val ZASHI_INTERNAL_DATA_FOSS_AUTHORITY_TESTNET_DEBUG = "co.electriccoin.zcash.foss.debug.provider.testnet"

    /**
     * Returns a new share internal app data intent with necessary permission granted exclusively to the data file.
     *
     * @param dataFilePath The private data file path we want to share
     *
     * @return Intent for launching an app for sharing
     */
    @Suppress("LongParameterList")
    internal fun newShareContentIntent(
        context: Context,
        dataFilePath: String,
        fileType: String,
        shareText: String? = null,
        sharePickerText: String,
        versionInfo: VersionInfo,
    ): Intent {
        return newShareContentIntent(
            context = context,
            file = File(dataFilePath),
            shareText = shareText,
            sharePickerText = sharePickerText,
            versionInfo = versionInfo,
            fileType = fileType,
        )
    }

    internal fun newShareContentIntent(
        context: Context,
        file: File,
        shareText: String? = null,
        sharePickerText: String,
        versionInfo: VersionInfo,
        fileType: String = ZASHI_INTERNAL_DATA_MIME_TYPE,
    ): Intent {
        val fileUri =
            FileProvider.getUriForFile(
                // context =
                context,
                // authority =
                getAuthorityByVersionInfo(versionInfo),
                // file =
                file
            )

        val dataIntent: Intent =
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                if (shareText != null) {
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                type = fileType
            }

        val shareDataIntent =
            Intent.createChooser(
                dataIntent,
                sharePickerText
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
            if (versionInfo.distributionDimension == DistributionDimension.FOSS) {
                getFossTestnetAuthority(versionInfo)
            } else {
                getStoreTestnetAuthority(versionInfo)
            }
        } else {
            if (versionInfo.distributionDimension == DistributionDimension.FOSS) {
                getFossMainnetAuthority(versionInfo)
            } else {
                getStoreMainnetAuthority(versionInfo)
            }
        }

    private fun getStoreMainnetAuthority(versionInfo: VersionInfo) =
        if (versionInfo.isDebuggable) {
            ZASHI_INTERNAL_DATA_AUTHORITY_DEBUG
        } else {
            ZASHI_INTERNAL_DATA_AUTHORITY
        }

    private fun getFossMainnetAuthority(versionInfo: VersionInfo) =
        if (versionInfo.isDebuggable) {
            ZASHI_INTERNAL_DATA_FOSS_AUTHORITY_DEBUG
        } else {
            ZASHI_INTERNAL_DATA_FOSS_AUTHORITY
        }

    private fun getStoreTestnetAuthority(versionInfo: VersionInfo) =
        if (versionInfo.isDebuggable) {
            ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET_DEBUG
        } else {
            ZASHI_INTERNAL_DATA_AUTHORITY_TESTNET
        }

    private fun getFossTestnetAuthority(versionInfo: VersionInfo) =
        if (versionInfo.isDebuggable) {
            ZASHI_INTERNAL_DATA_FOSS_AUTHORITY_TESTNET_DEBUG
        } else {
            ZASHI_INTERNAL_DATA_FOSS_AUTHORITY_TESTNET
        }
}
