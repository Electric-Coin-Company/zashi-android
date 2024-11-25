package co.electriccoin.zcash.ui.screen.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.INTEGRATIONS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveIsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.SensitiveSettingsVisibleUseCase
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.settings.model.SettingsState
import co.electriccoin.zcash.ui.screen.settings.model.SettingsTroubleshootingState
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingItemState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class SettingsViewModel(
    observeConfiguration: ObserveConfigurationUseCase,
    isSensitiveSettingsVisible: SensitiveSettingsVisibleUseCase,
    observeIsFlexaAvailable: ObserveIsFlexaAvailableUseCase,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val getVersionInfo: GetVersionInfoProvider,
    private val rescanBlockchain: RescanBlockchainUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    private val isAnalyticsEnabled = booleanStateFlow(StandardPreferenceKeys.IS_ANALYTICS_ENABLED)
    private val isBackgroundSyncEnabled = booleanStateFlow(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED)
    private val isKeepScreenOnWhileSyncingEnabled =
        booleanStateFlow(StandardPreferenceKeys.IS_KEEP_SCREEN_ON_DURING_SYNC)

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

    val state: StateFlow<SettingsState> =
        combine(
            troubleshootingState,
            isSensitiveSettingsVisible(),
            observeIsFlexaAvailable(),
        ) { troubleshootingState, isSensitiveSettingsVisible, isFlexaAvailable ->
            createState(
                troubleshootingState = troubleshootingState,
                isSensitiveSettingsVisible = isSensitiveSettingsVisible,
                isFlexaAvailable = isFlexaAvailable == true
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    troubleshootingState = null,
                    isSensitiveSettingsVisible = isSensitiveSettingsVisible().value,
                    isFlexaAvailable = observeIsFlexaAvailable().value == true
                )
        )

    private fun createState(
        troubleshootingState: SettingsTroubleshootingState?,
        isSensitiveSettingsVisible: Boolean,
        isFlexaAvailable: Boolean
    ) = SettingsState(
        debugMenu = troubleshootingState,
        onBack = ::onBack,
        items =
            listOfNotNull(
                ZashiSettingsListItemState(
                    text = stringRes(R.string.settings_address_book),
                    icon = R.drawable.ic_settings_address_book,
                    onClick = ::onAddressBookClick
                ),
                ZashiSettingsListItemState(
                    text = stringRes(R.string.settings_integrations),
                    icon = R.drawable.ic_settings_integrations,
                    onClick = ::onIntegrationsClick,
                    titleIcons =
                        listOfNotNull(
                            R.drawable.ic_integrations_coinbase,
                            R.drawable.ic_integrations_flexa.takeIf { isFlexaAvailable }
                        ).toImmutableList()
                ).takeIf { isSensitiveSettingsVisible },
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
            ).toImmutableList(),
        version = stringRes(R.string.settings_version, versionInfo.versionName)
    )

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

    fun onBack() = navigationRouter.back()

    private fun onIntegrationsClick() = navigationRouter.forward(INTEGRATIONS)

    private fun onAdvancedSettingsClick() = navigationRouter.forward(ADVANCED_SETTINGS)

    private fun onAboutUsClick() = navigationRouter.forward(ABOUT)

    private fun onSendUsFeedbackClick() = navigationRouter.forward(SUPPORT)

    private fun onAddressBookClick() = navigationRouter.forward(AddressBookArgs(AddressBookArgs.DEFAULT))

    private fun onWhatsNewClick() = navigationRouter.forward(WHATS_NEW)

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            emitAll(default.observe(standardPreferenceProvider()))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)
}
