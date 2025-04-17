package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface WalletBackupRemindMeCountStorageProvider : IntStorageProvider

class WalletBackupRemindMeCountStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseIntStorageProvider(key = PreferenceKey("wallet_backup_remind_me_count")),
    WalletBackupRemindMeCountStorageProvider
