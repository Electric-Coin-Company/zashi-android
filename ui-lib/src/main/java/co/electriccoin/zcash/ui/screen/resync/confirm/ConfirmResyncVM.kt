package co.electriccoin.zcash.ui.screen.resync.confirm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class ConfirmResyncVM : ViewModel() {
// class ConfirmResyncVM(
    // persistableWalletProvider: PersistableWalletProvider,
    // private val application: Application,
    // private val navigationRouter: NavigationRouter,
    // private val navigateToEstimateBlockHeight: NavigateToEstimateBlockHeightUseCase,
    // private val confirmResync: ConfirmResyncUseCase
// ) : ViewModel() {
    // private val blockHeight = MutableStateFlow<BlockHeight?>(null)

    // private var changeJob: Job? = null
    // private var confirmJob: Job? = null

    init {
        // viewModelScope.launch {
        //     val wallet = persistableWalletProvider.requirePersistableWallet()
        //     blockHeight.update { wallet.birthday }
        // }
    }

    val state: StateFlow<ConfirmResyncState?> = TODO()
    // blockHeight
    //     .map { height ->
    //         height?.let { createState(it) }
    //     }.stateIn(
    //         scope = viewModelScope,
    //         started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
    //         initialValue = null
    //     )

    // private suspend fun createState(height: BlockHeight): ConfirmResyncState {
    // val date = SdkSynchronizer
    //     .estimateBirthdayDate(application, height, VersionInfo.NETWORK)
    //
    // val yearMonth = if (date != null) {
    //     ZonedDateTime.ofInstant(date.toJavaInstant(), java.time.ZoneId.systemDefault())
    //         .let { YearMonth.of(it.year, it.month) }
    // } else {
    //     YearMonth.of(2018, 10)
    // }
    //
    // return ConfirmResyncState(
    //     title = stringRes(R.string.resync_title),
    //     subtitle = stringRes(R.string.confirm_resync_title),
    //     message = stringRes(R.string.confirm_resync_subtitle),
    //     onBack = ::onBack,
    //     confirm = ButtonState(
    //         stringRes(R.string.confirm_resync_confirm),
    //         hapticFeedbackType = HapticFeedbackType.Confirm,
    //         onClick = ::onConfirmClick
    //     ),
    //     change = ButtonState(
    //         stringRes(R.string.confirm_resync_change),
    //         onClick = ::onChangeClick
    //     ),
    //     changeInfo = styledStringResource(
    //         resource = R.string.confirm_resync_info,
    //         color = StringResourceColor.TERTIARY,
    //         fontWeight = FontWeight.Medium,
    //         styledStringResource(stringRes(yearMonth), StringResourceColor.PRIMARY, FontWeight.SemiBold),
    //         styledStringResource(stringResByNumber(height.value, 0), StringResourceColor.TERTIARY),
    //     )
    // )
    // }

    // private fun onBack() = navigationRouter.back()
    //
    // private fun onChangeClick() {
    //     if (changeJob?.isActive == true) return
    //     changeJob =
    //         viewModelScope.launch {
    //             val currentHeight = blockHeight.value ?: return@launch
    //             val result = navigateToEstimateBlockHeight(currentHeight)
    //             result?.let { blockHeight.update { result } }
    //             navigationRouter.backTo(ConfirmResyncArgs::class)
    //         }
    // }
    //
    // private fun onConfirmClick() {
    //     if (confirmJob?.isActive == true) return
    //     confirmJob = viewModelScope.launch { confirmResync(blockHeight.value ?: return@launch) }
    // }
}
