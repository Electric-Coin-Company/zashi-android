package model

enum class BuildType(
    val value: String
) {
    DEBUG("debug"),
    RELEASE("release"),
    BENCHMARK("benchmark")
}
