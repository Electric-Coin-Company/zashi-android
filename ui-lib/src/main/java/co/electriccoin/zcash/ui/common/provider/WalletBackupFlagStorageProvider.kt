package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface WalletBackupFlagStorageProvider : BooleanStorageProvider

class WalletBackupFlagStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseBooleanStorageProvider(key = PreferenceKey("wallet_backup_flag")),
    WalletBackupFlagStorageProvider
