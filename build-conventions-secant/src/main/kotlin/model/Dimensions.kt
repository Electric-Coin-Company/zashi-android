package model

enum class NetworkDimension(
    val value: String
) {
    MAINNET("zcashmainnet"),
    TESTNET("zcashtestnet");

    companion object {
        const val DIMENSION_NAME = "network"
    }
}

enum class DistributionDimension(
    val value: String
) {
    STORE("store"),
    FOSS("foss");

    companion object {
        const val DIMENSION_NAME = "distribution"
    }
}
