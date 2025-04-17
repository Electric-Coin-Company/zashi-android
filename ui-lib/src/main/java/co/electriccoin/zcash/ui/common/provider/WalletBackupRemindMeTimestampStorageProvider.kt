package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface WalletBackupRemindMeTimestampStorageProvider : TimestampStorageProvider

class WalletBackupRemindMeTimestampStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseTimestampStorageProvider(key = PreferenceKey("wallet_backup_remind_me_timestamp")),
    WalletBackupRemindMeTimestampStorageProvider
