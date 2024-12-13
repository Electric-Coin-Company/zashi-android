package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.util.FileShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SharePCZTUseCase(
    private val context: Context,
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val versionInfoProvider: GetVersionInfoProvider
) {
    suspend operator fun invoke() = withContext(Dispatchers.Main) {
        val proposalPczt = keystoneProposalRepository.getProposalPCZT() ?: return@withContext

        val file = getOrCreateFile()
        file.writeBytes(proposalPczt.toByteArray())

        val shareIntent =
            FileShareUtil.newShareContentIntent(
                context = context,
                dataFilePath = file.absolutePath,
                fileType = "*/octet-stream",
                shareText = "Pczt",
                sharePickerText = "pczt",
                versionInfo = versionInfoProvider(),
            )

        runCatching {
            context.startActivity(shareIntent)
        }
    }

    private fun getOrCreateFile(): File {
        val file = File(context.filesDir, "pczt")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}