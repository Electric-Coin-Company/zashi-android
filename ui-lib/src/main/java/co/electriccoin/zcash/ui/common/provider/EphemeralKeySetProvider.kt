package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.EphemeralAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface EphemeralAddressStorageProvider {

    fun observe(uuid: AccountUuid): Flow<EphemeralAddress?>

    suspend fun get(uuid: AccountUuid): EphemeralAddress?

    suspend fun store(uuid: AccountUuid, address: EphemeralAddress)

    suspend fun remove(uuid: AccountUuid)
}

class EphemeralAddressStorageProviderImpl(
    encryptedPreferenceProvider: EncryptedPreferenceProvider
) : EphemeralAddressStorageProvider {
    private val default = EphemeralAddressPreferenceDefault(encryptedPreferenceProvider)

    override fun observe(uuid: AccountUuid) = default.observe(uuid)

    override suspend fun get(uuid: AccountUuid): EphemeralAddress? = default.getValue(uuid)

    override suspend fun store(uuid: AccountUuid, address: EphemeralAddress) = default.putValue(address, uuid)

    override suspend fun remove(uuid: AccountUuid) = default.remove(uuid)
}

private class EphemeralAddressPreferenceDefault(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) {
    fun observe(uuid: AccountUuid): Flow<EphemeralAddress?> =
        flow { emitAll(encryptedPreferenceProvider().observe(key = getKey(uuid)).map { it?.decode() }) }

    suspend fun getValue(uuid: AccountUuid): EphemeralAddress? =
        encryptedPreferenceProvider().getString(key = getKey(uuid))?.decode()

    suspend fun putValue(address: EphemeralAddress?, uuid: AccountUuid) =
        encryptedPreferenceProvider().putString(key = getKey(uuid), value = address?.encode())

    suspend fun remove(uuid: AccountUuid) = encryptedPreferenceProvider().remove(key = getKey(uuid))

    @OptIn(ExperimentalStdlibApi::class)
    private fun getKey(uuid: AccountUuid) = PreferenceKey("ephemeral_address_${uuid.value.toHexString()}")
}

private fun EphemeralAddress?.encode(): String? = if (this == null) null else "$address.$gapPosition.$gapLimit"

private fun String?.decode(): EphemeralAddress? =
    this?.split(".")?.let {
        EphemeralAddress(
            address = it[0],
            gapPosition = it[1].toUInt(),
            gapLimit = it[2].toUInt()
        )
    }

