package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.InstallationPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.preference.model.entry.StringPreferenceDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class FlexaAccountIdProvider(
    private val installationPreferenceProvider: InstallationPreferenceProvider
) {
    private val preference = StringPreferenceDefault(PreferenceKey("FLEXA_ACCOUNT_ID"), "")

    suspend operator fun invoke(): String =
        withContext(Dispatchers.IO) {
            val existing = preference.getValue(installationPreferenceProvider())

            existing.ifEmpty {
                val new = UUID.randomUUID().toString().toSha256()
                preference.putValue(installationPreferenceProvider(), new)
                new
            }
        }

    private fun String.toSha256() =
        MessageDigest.getInstance("SHA-256")
            .digest(toByteArray())
            .fold("") { str, value -> str + "%02x".format(value) }
}
