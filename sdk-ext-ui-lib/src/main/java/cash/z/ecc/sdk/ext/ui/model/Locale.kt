package cash.z.ecc.sdk.ext.ui.model

data class Locale(val language: String, val region: String?, val variant: String?) {
    companion object
}

fun Locale.toJavaLocale(): java.util.Locale {
    return if (!region.isNullOrEmpty() && !variant.isNullOrEmpty()) {
        java.util.Locale(language, region, variant)
    } else if (!region.isNullOrEmpty() && variant.isNullOrEmpty()) {
        java.util.Locale(language, region)
    } else {
        java.util.Locale(language)
    }
}

fun java.util.Locale.toKotlinLocale(): Locale {
    val resultCountry = if (country.isNullOrEmpty()) {
        null
    } else {
        country
    }

    val resultVariant = if (variant.isNullOrEmpty()) {
        null
    } else {
        variant
    }

    return Locale(language, resultCountry, resultVariant)
}
