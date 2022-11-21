package co.electriccoin.zcash.ui.screen.backup.model

import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.spackle.model.Progress

sealed class BackupStage(internal val order: Int) {

    companion object {
        // Note: the indexes are used to manage progression through each stage
        // so be careful if changing these
        private const val EDUCATION_OVERVIEW_ORDER = 0
        private const val EDUCATION_RECOVERY_PHRASE_ORDER = 1
        private const val SEED_ORDER = 2
        private const val TEST_ORDER = 3
        private const val FAILURE_ORDER = 4
        private const val COMPLETE_ORDER = 5
        private const val CHECK_SEED_ORDER = 6

        val values: List<BackupStage> by lazy {
            arrayListOf<BackupStage>().apply {
                add(EDUCATION_OVERVIEW_ORDER, EducationOverview)
                add(EDUCATION_RECOVERY_PHRASE_ORDER, EducationRecoveryPhrase)
                add(SEED_ORDER, Seed)
                add(TEST_ORDER, Test)
                add(FAILURE_ORDER, Failure)
                add(COMPLETE_ORDER, Complete)
                add(CHECK_SEED_ORDER, CheckSeed)
            }
        }
    }

    object EducationOverview : BackupStage(EDUCATION_OVERVIEW_ORDER)
    object EducationRecoveryPhrase : BackupStage(EDUCATION_RECOVERY_PHRASE_ORDER)
    object Seed : BackupStage(SEED_ORDER)
    object Test : BackupStage(TEST_ORDER) {
        // To bypass the Failure state
        override fun getNext(): BackupStage {
            return values[COMPLETE_ORDER]
        }
    }
    object Failure : BackupStage(FAILURE_ORDER) {
        // To let user preview the seed again after test failure
        override fun getPrevious(): BackupStage {
            return values[SEED_ORDER]
        }
    }
    object Complete : BackupStage(COMPLETE_ORDER) {
        // To disable back navigation after successful test
        override fun hasPrevious(): Boolean {
            return false
        }
    }
    object CheckSeed : BackupStage(CHECK_SEED_ORDER)

    /**
     * @see getPrevious
     */
    open fun hasPrevious() = order > 0

    /**
     * @see getNext
     */
    open fun hasNext() = order < values.size - 1

    /**
     * @return Previous item in ordinal order.  Returns the first item when it cannot go further back.
     */
    open fun getPrevious() = values[maxOf(0, order - 1)]

    /**
     * @return Last item in ordinal order.  Returns the last item when it cannot go further forward.
     */
    open fun getNext() = values[minOf(values.size - 1, order + 1)]

    /**
     * @return Returns current progression through stages.
     */
    fun getProgress() = Progress(Index(order), Index(values.size - 1))
}
