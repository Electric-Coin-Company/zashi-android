package co.electriccoin.zcash.ui.common

import kotlin.time.Duration.Companion.seconds

// Recommended timeout for Android configuration changes to keep Kotlin Flow from restarting
val ANDROID_STATE_FLOW_TIMEOUT = 5.seconds
const val SIDE_SHIFT_AFFILIATE_LINK = "https://sideshift.ai/a/EqcQp4iUM"
const val STEALTH_HEALTH_AFFILIATE_LINK = "https://stealthex.io/?ref=x80l5bu8wq"
const val ZCASH_LEARN_MORE_LINK = "https://z.cash/the-basics/"
const val ZEC_MAX_AMOUNT = 21000000
const val AMOUNT_QUERY = "amount"
const val MEMO_QUERY = "memo"
const val LEARN_UNIFIED_ADDRESSES = "https://electriccoin.co/blog/unified-addresses-in-zcash-explained/"
const val WALLET_PASSWORD_LENGTH = 6
val SUCCESS_VIBRATION_PATTERN = arrayOf(0L, 200L, 100L, 100L, 800L).toLongArray()
val WRONG_VIBRATION_PATTERN = arrayOf(0L, 50L, 100L, 50L, 100L).toLongArray()
const val WORKER_TAG_SYNC_NOTIFICATION = "constants.tag_sync_notification"
const val SEED_PHRASE = "cash.z.ecc.android.SEED_PHRASE"
const val HAS_SEED_PHRASE = "cash.z.ecc.android.HAS_SEED_PHRASE"
const val BIRTHDAY_HEIGHT = "cash.z.ecc.android.BIRTHDAY_HEIGHT"
const val PIN_CODE = "const.pin.code"
const val IS_BIO_METRIC_OR_FACE_ID_ENABLED = "const.pin.is_biometric_or_face_id"
const val MIN_ZEC_FOR_SHIELDING = 0.01