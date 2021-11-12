# Gathering Code Coverage
The app consists of different Gradle module types (e.g. Kotlin Multiplatform, Android).  Generating coverage for these different module types requires different command line invocations.

## Kotlin Multiplatform
Kotlin Multiplatform does not support coverage for all platforms.  Most of our code lives under commonMain, with a JVM target.  This effectively allows generation of coverage reports with Jacoco.

Due to some quirks with the Jacoco integration, coverage must be generated in two Gradle invocations like this:

`./gradlew test -x connectedCheck -PIS_COVERAGE_ENABLED=true; ./gradlew jacocoTestReport -PIS_COVERAGE_ENABLED=true`

## Android
The Android Gradle plugin supports code coverage with Jacoco.  This integration can sometimes be buggy.  For that reason, coverage is disabled by default and can be enabled on a case-by-case basis, by passing `-PIS_COVERAGE_ENABLED=true` as a command line argument for Gradle builds.  For example: `./gradlew :app:connectedCheck -PIS_COVERAGE_ENABLED=true`.

When coverage is enabled, running instrumentation tests will automatically generate coverage reports stored under `build/reports/coverage`.