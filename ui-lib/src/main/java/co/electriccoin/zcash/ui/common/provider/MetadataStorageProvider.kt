package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import java.io.File

interface MetadataStorageProvider {
    fun getStorageFile(key: MetadataKey): File?

    fun getOrCreateStorageFile(key: MetadataKey): File
}

class MetadataStorageProviderImpl(
    private val context: Context
) : MetadataStorageProvider {
    override fun getStorageFile(key: MetadataKey): File? {
        return File(getOrCreateMetadataDir(), key.fileIdentifier())
            .takeIf { it.exists() && it.isFile }
    }

    override fun getOrCreateStorageFile(key: MetadataKey): File {
        val file = File(getOrCreateMetadataDir(), key.fileIdentifier())
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun getOrCreateMetadataDir(): File {
        val filesDir = context.filesDir
        val addressBookDir = File(filesDir, "metadata")
        if (!addressBookDir.exists()) {
            addressBookDir.mkdir()
        }
        return addressBookDir
    }
}
