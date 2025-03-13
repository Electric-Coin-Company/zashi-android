package co.electriccoin.zcash.ui.screen.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.GetWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.integrations.Integrations
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
    getWalletAccounts: GetWalletAccountsUseCase,
    isFlexaAvailable: IsFlexaAvailableUseCase,
    isCoinbaseAvailable: IsCoinbaseAvailableUseCase,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val getVersionInfo: GetVersionInfoProvider,
    private val rescanBlockchain: RescanBlockchainUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToAddressBook: NavigateToAddressBookUseCase
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
            isKeepScreenOnWhileSyncingEnabled,
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
            getWalletAccounts.observe(),
            isFlexaAvailable.observe(),
            isCoinbaseAvailable.observe(),
        ) { troubleshootingState, accounts, isFlexaAvailable, isCoinbaseAvailable ->
            createState(
                selectedAccount = accounts?.firstOrNull { it.isSelected },
                troubleshootingState = troubleshootingState,
                isFlexaAvailable = isFlexaAvailable == true,
                isCoinbaseAvailable = isCoinbaseAvailable == true,
                isKeystoneAvailable = accounts?.none { it is KeystoneAccount } == true
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    selectedAccount = getWalletAccounts.observe().value?.firstOrNull { it.isSelected },
                    troubleshootingState = null,
                    isFlexaAvailable = isFlexaAvailable.observe().value == true,
                    isCoinbaseAvailable = isCoinbaseAvailable.observe().value == true,
                    isKeystoneAvailable = getWalletAccounts.observe().value?.none { it is KeystoneAccount } == true
                )
        )

    private fun createState(
        selectedAccount: WalletAccount?,
        troubleshootingState: SettingsTroubleshootingState?,
        isFlexaAvailable: Boolean,
        isCoinbaseAvailable: Boolean,
        isKeystoneAvailable: Boolean
    ) = SettingsState(
        debugMenu = troubleshootingState,
        onBack = ::onBack,
        items =
            listOfNotNull(
                ZashiListItemState(
                    title = stringRes(R.string.settings_address_book),
                    icon = R.drawable.ic_settings_address_book,
                    onClick = ::onAddressBookClick
                ),
                ZashiListItemState(
                    title = stringRes(R.string.settings_integrations),
                    icon =
                        when (selectedAccount) {
                            is KeystoneAccount -> R.drawable.ic_settings_integrations_disabled
                            is ZashiAccount -> R.drawable.ic_settings_integrations
                            null -> R.drawable.ic_settings_integrations
                        },
                    onClick = ::onIntegrationsClick,
                    isEnabled = selectedAccount is ZashiAccount,
                    subtitle =
                        stringRes(R.string.settings_integrations_subtitle_disabled).takeIf {
                            selectedAccount !is ZashiAccount
                        },
                    titleIcons =
                        listOfNotNull(
                            when (selectedAccount) {
                                is KeystoneAccount -> R.drawable.ic_integrations_coinbase_disabled
                                is ZashiAccount -> R.drawable.ic_integrations_coinbase
                                null -> R.drawable.ic_integrations_coinbase
                            }.takeIf { isCoinbaseAvailable },
                            when (selectedAccount) {
                                is KeystoneAccount -> R.drawable.ic_integrations_flexa_disabled
                                is ZashiAccount -> R.drawable.ic_integrations_flexa
                                null -> R.drawable.ic_integrations_flexa
                            }.takeIf { isFlexaAvailable },
                            R.drawable.ic_integrations_keystone
                                .takeIf { isKeystoneAvailable }
                        ).toImmutableList()
                ).takeIf { isFlexaAvailable || isCoinbaseAvailable || isKeystoneAvailable },
                ZashiListItemState(
                    title = stringRes(R.string.settings_advanced_settings),
                    icon = R.drawable.ic_advanced_settings,
                    onClick = ::onAdvancedSettingsClick
                ),
                ZashiListItemState(
                    title = stringRes(R.string.settings_whats_new),
                    icon = R.drawable.ic_settings_whats_new,
                    onClick = ::onWhatsNewClick
                ),
                ZashiListItemState(
                    title = stringRes(R.string.settings_about_us),
                    icon = R.drawable.ic_settings_info,
                    onClick = ::onAboutUsClick
                ),
                ZashiListItemState(
                    title = stringRes(R.string.settings_feedback),
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

    private fun onIntegrationsClick() = navigationRouter.forward(Integrations)

    private fun onAdvancedSettingsClick() = navigationRouter.forward(ADVANCED_SETTINGS)

    private fun onAboutUsClick() = navigationRouter.forward(ABOUT)

    private fun onSendUsFeedbackClick() = navigationRouter.forward(SUPPORT)

    private fun onAddressBookClick() =
        viewModelScope.launch {
            navigateToAddressBook(AddressBookArgs.DEFAULT)
        }

    private fun onWhatsNewClick() = navigationRouter.forward(WHATS_NEW)

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            emitAll(default.observe(standardPreferenceProvider()))
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)
}
