package co.electriccoin.zcash.preference.model.entry

import co.electriccoin.zcash.preference.api.PreferenceProvider

/**
 * An entry represents a key and a default value for a preference.  By using a Default object,
 * multiple parts of the code can fetch the same preference without duplication or accidental
 * variation in default value.  Clients define the key and default value together, rather than just
 * the key.
 */
/*
 * API note: the default value is not available through the public interface in order to prevent
 * clients from accidentally using the default value instead of the preference value.
 *
 * Implementation note: although primitives would be nice, Objects don't increase memory usage much.
 * The autoboxing cache solves Booleans, and Strings are already objects, so that just leaves Integers.
 * Overall the number of Integer preference entries is expected to be low compared to Booleans,
 * and perhaps many Integer values will also fit within the autoboxing cache.
 */
interface PreferenceDefault<T> {

    val key: Key

    /**
     * @param preferenceProvider Provides actual preference values
     * @return The value in the preference, or the default value if no preference exists.
     */
    suspend fun getValue(preferenceProvider: PreferenceProvider): T
}
