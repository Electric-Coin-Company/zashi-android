package co.electriccoin.zcash.ui.screen.send.nighthawk.model

enum class SendUIState(private val step: Int) {
    ENTER_ZEC(0),
    ENTER_MESSAGE(1),
    ENTER_ADDRESS(2),
    REVIEW_AND_SEND(3),
    SEND_CONFIRMATION(4);

    open fun hasNext() = step < SEND_CONFIRMATION.step

    open fun hasPrevious() = step > ENTER_ZEC.step

    open fun getNext(currentUIState: SendUIState) = if (currentUIState.hasNext()) values()[currentUIState.step + 1] else null

    open fun getPrevious(currentUIState: SendUIState) = if (currentUIState.hasPrevious()) values()[currentUIState.step - 1] else null
}
