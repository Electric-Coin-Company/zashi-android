package model

sealed class ZashiBuildType {
    abstract val name: String

    object Debug : ZashiBuildType() {
        override val name = "debug"
    }
    object Benchmark : ZashiBuildType() {
        override val name = "benchmark"
    }
    object Release : ZashiBuildType() {
        override val name = "release"
    }
    object Foss : ZashiBuildType() {
        override val name = "foss"
    }
}