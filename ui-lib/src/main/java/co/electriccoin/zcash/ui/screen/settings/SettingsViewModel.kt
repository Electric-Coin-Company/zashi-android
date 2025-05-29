package co.electriccoin.zcash.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets.ABOUT
import co.electriccoin.zcash.ui.NavigationTargets.ADVANCED_SETTINGS
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getVersionInfo: GetVersionInfoProvider,
    private val navigationRouter: NavigationRouter,
    private val navigateToAddressBook: NavigateToAddressBookUseCase,
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    val state: StateFlow<SettingsState?> = MutableStateFlow(createState())

    private fun createState() =
        SettingsState(
            version = stringRes(R.string.settings_version, versionInfo.versionName),
            onBack = ::onBack,
            items =
                listOfNotNull(
                    ListItemState(
                        title = stringRes(R.string.settings_address_book),
                        icon = imageRes(R.drawable.ic_settings_address_book),
                        onClick = ::onAddressBookClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_advanced_settings),
                        icon = imageRes(R.drawable.ic_advanced_settings),
                        onClick = ::onAdvancedSettingsClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_whats_new),
                        icon = imageRes(R.drawable.ic_settings_whats_new),
                        onClick = ::onWhatsNewClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_about_us),
                        icon = imageRes(R.drawable.ic_settings_info),
                        onClick = ::onAboutUsClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_feedback),
                        icon = imageRes(R.drawable.ic_settings_feedback),
                        onClick = ::onSendUsFeedbackClick
                    ),
                ).toImmutableList()
        )

    private fun onBack() = navigationRouter.back()

    private fun onAdvancedSettingsClick() = navigationRouter.forward(ADVANCED_SETTINGS)

    private fun onAboutUsClick() = navigationRouter.forward(ABOUT)

    private fun onSendUsFeedbackClick() = navigationRouter.forward(SUPPORT)

    private fun onAddressBookClick() = viewModelScope.launch { navigateToAddressBook(AddressBookArgs.DEFAULT) }

    private fun onWhatsNewClick() = navigationRouter.forward(WHATS_NEW)
}
