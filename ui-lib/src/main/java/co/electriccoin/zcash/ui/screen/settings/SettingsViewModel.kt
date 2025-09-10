package co.electriccoin.zcash.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets.SUPPORT
import co.electriccoin.zcash.ui.NavigationTargets.WHATS_NEW
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.about.AboutArgs
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsArgs
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getVersionInfo: GetVersionInfoProvider,
    private val navigationRouter: NavigationRouter,
    private val navigateToAddressBook: NavigateToAddressBookUseCase,
) : ViewModel() {
    val state: StateFlow<SettingsState> = MutableStateFlow(createState())

    private fun createState() =
        SettingsState(
            version = stringRes(R.string.settings_version, getVersionInfo().versionName),
            onBack = ::onBack,
            items =
                listOfNotNull(
                    ListItemState(
                        title = stringRes(R.string.settings_address_book),
                        bigIcon = imageRes(R.drawable.ic_settings_address_book),
                        onClick = ::onAddressBookClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_advanced_settings),
                        bigIcon = imageRes(R.drawable.ic_advanced_settings),
                        onClick = ::onAdvancedSettingsClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_whats_new),
                        bigIcon = imageRes(R.drawable.ic_settings_whats_new),
                        onClick = ::onWhatsNewClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_about_us),
                        bigIcon = imageRes(R.drawable.ic_settings_info),
                        onClick = ::onAboutUsClick
                    ),
                    ListItemState(
                        title = stringRes(R.string.settings_feedback),
                        bigIcon = imageRes(R.drawable.ic_settings_feedback),
                        onClick = ::onSendUsFeedbackClick
                    ),
                ).toImmutableList()
        )

    private fun onBack() = navigationRouter.back()

    private fun onAdvancedSettingsClick() = navigationRouter.forward(AdvancedSettingsArgs)

    private fun onAboutUsClick() = navigationRouter.forward(AboutArgs)

    private fun onSendUsFeedbackClick() = navigationRouter.forward(SUPPORT)

    private fun onAddressBookClick() = viewModelScope.launch { navigateToAddressBook() }

    private fun onWhatsNewClick() = navigationRouter.forward(WHATS_NEW)
}
