# Gathering Code Coverage
The app consists of different Gradle module types (e.g. Kotlin Multiplatform, Android).  Generating coverage for these different module types requires different command line invocations.

## Kotlin Multiplatform
Kotlin Multiplatform does not support coverage for all platforms.  Most of our code lives under commonMain, with a JVM target.  This effectively allows generation of coverage reports with Jacoco.  Coverage is enabled by default when running `./gradlew check`.

## Android
The Android Gradle plugin supports code coverage with Jacoco.  This integration can sometimes be buggy.  For that reason, coverage is disabled by default and can be enabled on a case-by-case basis, by passing `-PIS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED=true` as a command line argument for Gradle builds.  For example: `./gradlew connectedCheck -PIS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED=true`.

When coverage is enabled, running instrumentation tests will automatically generate coverage reports stored under `$module/build/reports/coverage`.
