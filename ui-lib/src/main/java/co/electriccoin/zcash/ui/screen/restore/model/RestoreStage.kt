package co.electriccoin.zcash.ui.screen.restore.model

enum class RestoreStage {
    // Note: the ordinal order is used to manage progression through each stage
    // so be careful if reordering these
    Seed,
    Birthday;

    /**
     * @see getPrevious
     */
    fun hasPrevious() = ordinal > 0

    /**
     * @see getNext
     */
    fun hasNext() = ordinal < entries.size - 1

    /**
     * @return Previous item in ordinal order.  Returns the first item when it cannot go further back.
     */
    fun getPrevious() = entries[maxOf(0, ordinal - 1)]

    /**
     * @return Last item in ordinal order.  Returns the last item when it cannot go further forward.
     */
    fun getNext() = entries[minOf(entries.size - 1, ordinal + 1)]
}
