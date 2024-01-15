package co.electriccoin.zcash.global

import androidx.core.content.FileProvider
import co.electriccoin.zcash.ui.R

/**
 * Internal content provider for the private data database file.
 */
internal class ShareFileProvider : FileProvider(R.xml.share_file_provider_paths)
