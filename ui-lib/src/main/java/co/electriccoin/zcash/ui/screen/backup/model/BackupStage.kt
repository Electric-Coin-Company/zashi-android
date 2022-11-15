package co.electriccoin.zcash.ui.screen.backup.model

import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.spackle.model.Progress

sealed class BackupStage(internal val order: Int) {

    companion object {
        // Note: the order index is used to manage progression through each stage
        // so be careful if reordering these
        private const val EDUCATION_OVERVIEW_ORDER = 0
        private const val EDUCATION_RECOVERY_PHRASE_ORDER = 1
        private const val SEED_ORDER = 2
        private const val TEST_ORDER = 3
        private const val COMPLETE_ORDER = 4

        // This is a rough way how to minimize orders handling
        fun values(): Array<BackupStage> = arrayListOf<BackupStage>().apply {
            EducationOverview.let { add(it.order, it) }
            EducationRecoveryPhrase.let { add(it.order, it) }
            Seed.let { add(it.order, it) }
            Test(Test.TestStage.InProgress).let { add(it.order, it) }
            Complete.let { add(it.order, it) }
        }.toTypedArray()
    }

    object EducationOverview : BackupStage(EDUCATION_OVERVIEW_ORDER)
    object EducationRecoveryPhrase : BackupStage(EDUCATION_RECOVERY_PHRASE_ORDER)
    object Seed : BackupStage(SEED_ORDER)
    class Test(var testStage: TestStage) : BackupStage(TEST_ORDER) {
        /**
         * These two Test special stages match two Test screen variants.
         * The Test success variant is a normal Complete stage.
         */
        enum class TestStage {
            InProgress,
            Failure;
        }
    }
    object Complete : BackupStage(COMPLETE_ORDER)

    /**
     * @see getPrevious
     */
    fun hasPrevious() = order > 0

    /**
     * @see getNext
     */
    fun hasNext() = order < values().size - 1

    /**
     * @return Previous item in ordinal order.  Returns the first item when it cannot go further back.
     */
    fun getPrevious() = values()[maxOf(0, order - 1)]

    /**
     * @return Last item in ordinal order.  Returns the last item when it cannot go further forward.
     */
    fun getNext() = values()[minOf(values().size - 1, order + 1)]

    /**
     * @return Last item in ordinal order.  Returns the last item when it cannot go further forward.
     */
    fun getProgress() = Progress(Index(order), Index(values().size - 1))
}
