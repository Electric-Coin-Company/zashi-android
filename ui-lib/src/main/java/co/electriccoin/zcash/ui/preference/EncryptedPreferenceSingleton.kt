package co.electriccoin.zcash.ui.preference

// import android.content.Context
// import co.electriccoin.zcash.preference.AndroidPreferenceProvider
// import co.electriccoin.zcash.preference.api.PreferenceProvider
// import co.electriccoin.zcash.spackle.SuspendingLazy

// object EncryptedPreferenceSingleton {
//     private const val PREF_FILENAME = "co.electriccoin.zcash.encrypted"
//
//     private val lazy =
//         SuspendingLazy<Context, PreferenceProvider> {
//             AndroidPreferenceProvider.newEncrypted(it, PREF_FILENAME)
//         }
//
//     suspend fun getInstance(context: Context) = lazy.getInstance(context)
// }
