package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey

interface WalletBackupConsentStorageProvider : BooleanStorageProvider

class WalletBackupConsentStorageProviderImpl(
    override val preferenceHolder: EncryptedPreferenceProvider
) : BaseBooleanStorageProvider(key = PreferenceKey("wallet_backup_consent")),
    WalletBackupConsentStorageProvider
