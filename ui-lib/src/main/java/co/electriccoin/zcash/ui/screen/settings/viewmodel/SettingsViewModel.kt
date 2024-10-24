package co.electriccoin.zcash.ui.screen.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.INTEGRATIONS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.settings.model.SettingsState
import co.electriccoin.zcash.ui.screen.settings.model.SettingsTroubleshootingState
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingItemState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class SettingsViewModel(
    observeConfiguration: ObserveConfigurationUseCase,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val getVersionInfo: GetVersionInfoProvider,
    private val rescanBlockchain: RescanBlockchainUseCase
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    private val isAnalyticsEnabled = booleanStateFlow(StandardPreferenceKeys.IS_ANALYTICS_ENABLED)
    private val isBackgroundSyncEnabled = booleanStateFlow(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED)
    private val isKeepScreenOnWhileSyncingEnabled =
        booleanStateFlow(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC)

    private val isLoading =
        combine(
            isAnalyticsEnabled,
            isBackgroundSyncEnabled,
            isKeepScreenOnWhileSyncingEnabled
        ) { isAnalyticsEnabled, isBackgroundSync, isKeepScreenOnWhileSyncing ->
            isAnalyticsEnabled == null || isBackgroundSync == null || isKeepScreenOnWhileSyncing == null
        }.distinctUntilChanged()

    @Suppress("ComplexCondition")
    private val troubleshootingState =
        combine(
            observeConfiguration(),
            isAnalyticsEnabled,
            isBackgroundSyncEnabled,
            isKeepScreenOnWhileSyncingEnabled
        ) { configuration, isAnalyticsEnabled, isBackgroundSyncEnabled, isKeepScreenOnWhileSyncingEnabled ->
            if (configuration != null &&
                isAnalyticsEnabled != null &&
                isBackgroundSyncEnabled != null &&
                isKeepScreenOnWhileSyncingEnabled != null &&
                versionInfo.isDebuggable &&
                !versionInfo.isRunningUnderTestService
            ) {
                SettingsTroubleshootingState(
                    backgroundSync =
                        TroubleshootingItemState(
                            isBackgroundSyncEnabled
                        ) { setBackgroundSyncEnabled(isBackgroundSyncEnabled.not()) },
                    keepScreenOnDuringSync =
                        TroubleshootingItemState(
                            isKeepScreenOnWhileSyncingEnabled
                        ) { setKeepScreenOnWhileSyncing(isKeepScreenOnWhileSyncingEnabled.not()) },
                    analytics =
                        TroubleshootingItemState(
                            isAnalyticsEnabled
                        ) { setAnalyticsEnabled(isAnalyticsEnabled.not()) },
                    rescan =
                        TroubleshootingItemState(
                            ConfigurationEntries.IS_RESCAN_ENABLED.getValue(configuration),
                            ::onRescanBlockchainClick
                        )
                )
            } else {
                null
            }
        }

    val state: StateFlow<SettingsState?> =
        combine(isLoading, troubleshootingState) { isLoading, troubleshootingState ->
            SettingsState(
                isLoading = isLoading,
                debugMenu = troubleshootingState,
                onBack = ::onBack,
                items =
                    persistentListOf(
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.settings_address_book),
                            icon = R.drawable.ic_settings_address_book,
                            onClick = ::onAddressBookClick
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.settings_integrations),
                            icon = R.drawable.ic_settings_integrations,
                            onClick = ::onIntegrationsClick,
                            titleIcons = persistentListOf(R.drawable.ic_integrations_coinbase)
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.settings_advanced_settings),
                            icon = R.drawable.ic_advanced_settings,
                            onClick = ::onAdvancedSettingsClick
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.settings_whats_new),
                            icon = R.drawable.ic_settings_whats_new,
                            onClick = ::onWhatsNewClick
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.settings_about_us),
                            icon = R.drawable.ic_settings_info,
                            onClick = ::onAboutUsClick
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.settings_feedback),
                            icon = R.drawable.ic_settings_feedback,
                            onClick = ::onSendUsFeedbackClick
                        ),
                    ),
                version = stringRes(R.string.settings_version, versionInfo.versionName)
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    val navigationCommand = MutableSharedFlow<String>()
    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun setAnalyticsEnabled(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_ANALYTICS_ENABLED, enabled)
    }

    private fun setBackgroundSyncEnabled(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED, enabled)
    }

    private fun setKeepScreenOnWhileSyncing(enabled: Boolean) {
        setBooleanPreference(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC, enabled)
    }

    private fun onRescanBlockchainClick() =
        viewModelScope.launch {
            rescanBlockchain()
        }

    private fun setBooleanPreference(
        default: BooleanPreferenceDefault,
        newState: Boolean
    ) {
        viewModelScope.launch {
            default.putValue(standardPreferenceProvider(), newState)
        }
    }

    fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onIntegrationsClick() =
        viewModelScope.launch {
            navigationCommand.emit(INTEGRATIONS)
        }

    private fun onAdvancedSettingsClick() =
        viewModelScope.launch {
            navigationCommand.emit(ADVANCED_SETTINGS)
        }

    private fun onAboutUsClick() =
        viewModelScope.launch {
            navigationCommand.emit(ABOUT)
        }

    private fun onSendUsFeedbackClick() =
        viewModelScope.launch {
            navigationCommand.emit(SUPPORT)
        }

    private fun onAddressBookClick() {
        viewModelScope.launch {
            navigationCommand.emit(AddressBookArgs(AddressBookArgs.DEFAULT))
        }
    }

    private fun onWhatsNewClick() {
        viewModelScope.launch {
            navigationCommand.emit(WHATS_NEW)
        }
    }

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            emitAll(default.observe(standardPreferenceProvider()))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)
}
