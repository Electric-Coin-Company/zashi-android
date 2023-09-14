package co.electriccoin.zcash.ui.screen.migration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.BIRTHDAY_HEIGHT
import co.electriccoin.zcash.ui.common.IS_BIO_METRIC_OR_FACE_ID_ENABLED
import co.electriccoin.zcash.ui.common.OldSecurePreference
import co.electriccoin.zcash.ui.common.PIN_CODE
import co.electriccoin.zcash.ui.common.SEED_PHRASE
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.migration.model.AppMigrationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppMigrationViewModel(private val context: Application) : AndroidViewModel(application = context) {

    private val oldSecurePreference by lazy { OldSecurePreference(context) }

    private val _appMigrationState = MutableStateFlow<AppMigrationState>(AppMigrationState.Idle)

    val appMigrationState: StateFlow<AppMigrationState> get() = _appMigrationState

    fun checkForOldAppMigration() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val birthdayHeight = oldSecurePreference.getLong(BIRTHDAY_HEIGHT)
                val seedPhraseString = oldSecurePreference.getString(SEED_PHRASE)
                val network = ZcashNetwork.fromResources(context)
                val birthday = birthdayHeight?.let { BlockHeight.new(network, birthdayHeight) }
                val seedPhrase = SeedPhrase.new(seedPhraseString)
                val oldPin = oldSecurePreference.getString(PIN_CODE).removeSurrounding("[", "]")
                    .replace(",", "").replace(" ", "")
                val isBioMetricEnabled = oldSecurePreference.getBoolean(IS_BIO_METRIC_OR_FACE_ID_ENABLED)
                updatePasswordAndBioMetric(oldPin, isBioMetricEnabled)
                _appMigrationState.update {
                    AppMigrationState.DataRecovered(
                        PersistableWallet(
                            network = network,
                            birthday = birthday,
                            seedPhrase = seedPhrase,
                            walletInitMode = WalletInitMode.RestoreWallet
                        )
                    )
                }
            } catch (e: Exception) {
                _appMigrationState.update { AppMigrationState.Error(throwable = e) }
                Twig.info { "Exception while checking for oldAppMigration $e" }
            }
        }
    }

    private fun updatePasswordAndBioMetric(oldPin: String, bioMetricEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            oldPin.isNotBlank().let {
                val preferenceProvider = StandardPreferenceSingleton.getInstance(context)
                StandardPreferenceKeys.LAST_ENTERED_PIN.putValue(preferenceProvider, oldPin)
                StandardPreferenceKeys.IS_TOUCH_ID_OR_FACE_ID_ENABLED.putValue(preferenceProvider, bioMetricEnabled)
            }
        }
    }

    fun clearOldData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                oldSecurePreference.clear()
            } catch (e: Exception) {
                Twig.info { "Exception while clearing data from oldAppMigration $e" }
            }
        }
    }
}
