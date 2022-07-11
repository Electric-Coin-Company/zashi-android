package cash.z.ecc.sdk.ext.ui.model

data class Locale(val language: String, val region: String?, val variant: String?) {
    companion object
}

fun Locale.toJavaLocale(): java.util.Locale {
    return if (region != null && variant != null) {
        java.util.Locale(language, region, variant)
    } else if (region != null && variant == null) {
        java.util.Locale(language, region)
    } else {
        java.util.Locale(language)
    }
}

fun java.util.Locale.toKotlinLocale(): Locale {
    return Locale(language, country, variant)
}
