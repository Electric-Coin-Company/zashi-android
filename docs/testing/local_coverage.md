# Gathering Code Coverage
The Android Gradle plugin supports code coverage with Jacoco.  This integration can sometimes be buggy.  For that reason, coverage is disabled by default and can be enabled on a case-by-case basis, by passing `-PIS_COVERAGE_ENABLED=true` as a command line argument for Gradle builds.  For example: `./gradlew :app:connectedCheck -PIS_COVERAGE_ENABLED=true`.

When coverage is enabled, running instrumentation tests will automatically generate coverage reports stored under `build/reports/coverage`.