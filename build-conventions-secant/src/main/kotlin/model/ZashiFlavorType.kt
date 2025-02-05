package model

const val ZASHI_FLAVOR_DIMENSION = "network"

// We would rather name them "testnet" and "mainnet" but product flavor names cannot start with the word "test"
sealed class ZashiFlavorType {
    abstract val name: String

    object Mainnet : ZashiBuildType() {
        override val name = "zcashmainnet"
    }
    object Testnet : ZashiBuildType() {
        override val name = "zcashtestnet"
    }
}