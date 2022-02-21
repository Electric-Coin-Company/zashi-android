package cash.z.ecc.ui.screen.backup.model

import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.spackle.model.Progress

enum class BackupStage {
    // Note: the ordinal order is used to manage progression through each stage
    // so be careful if reordering these
    EducationOverview,
    EducationRecoveryPhrase,
    Seed,
    Test,
    Complete;

    /**
     * @see getPrevious
     */
    fun hasPrevious() = ordinal > 0

    /**
     * @see getNext
     */
    fun hasNext() = ordinal < values().size - 1

    /**
     * @return Previous item in ordinal order.  Returns the first item when it cannot go further back.
     */
    fun getPrevious() = values()[maxOf(0, ordinal - 1)]

    /**
     * @return Last item in ordinal order.  Returns the last item when it cannot go further forward.
     */
    fun getNext() = values()[minOf(values().size - 1, ordinal + 1)]

    /**
     * @return Last item in ordinal order.  Returns the last item when it cannot go further forward.
     */
    fun getProgress() = Progress(Index(ordinal), Index(values().size - 1))
}
