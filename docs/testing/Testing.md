# Testing
This documentation outlines our approach to testing. By running tests against our app consistently, we verify the app's correctness, functional behavior, and usability before releasing it publicly.

## Automated testing

- TBD
<!-- TODO [#682]: Testing documentation update --> 
<!-- TODO [#682]: https://github.com/zcash/secant-android-wallet/issues/682 --> 

## Manual testing

We aim to automate as much as we possibly can. Still manual testing is really important for Quality Assurance.

Here you'll find our manual testing scripts. When developing a new feature you can add your own that provide the proper steps to properly test it.

## Gathering Code Coverage
The app consists of different Gradle module types (e.g. Kotlin Multiplatform, Android).  Generating coverage for these different module types requires different command line invocations.

### Kotlin Multiplatform
Kotlin Multiplatform does not support coverage for all platforms.  Most of our code lives under commonMain, with a JVM target.  This effectively allows generation of coverage reports with Jacoco.  Coverage is enabled by default when running `./gradlew check`.

### Android
The Android Gradle plugin supports code coverage with Jacoco.  This integration can sometimes be buggy.  For that reason, coverage is disabled by default and can be enabled on a case-by-case basis, by passing `-PIS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED=true` as a command line argument for Gradle builds.  For example: `./gradlew connectedCheck -PIS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED=true`.

When coverage is enabled, running instrumentation tests will automatically generate coverage reports stored under `$module/build/reports/coverage`.

## Benchmarking
This section provides information about available benchmark tests integrated in this project as well as how to use them. Currently, we support macrobenchmark tests run locally as described in the Android [documentation](https://developer.android.com/topic/performance/benchmarking/benchmarking-overview).

We provide dedicated benchmark test module `ui-benchamark-test` for this. If you want to run these benchmark tests against our application, make sure you have a physical device connected with Android SDK level 29, at least. Select `zcashmainnetBenchmark` or `zcashtestnetBenchmark` build variant for this module. Make sure that other modules are set to release variants of their available build variants too, as benchmarking is only allowed against minified build variants. The benchmark tests can be run with Android Studio run configuration `ui-benchmark-test:connectedZcashmainnetBenchmarkAndroidTest` with having the Gradle property `IS_SIGN_RELEASE_BUILD_WITH_DEBUG_KEY` set to true. Running the benchmark test this way automatically provides benchmarking results in Run panel. Or you can run the tests manually from the terminal with `./gradlew connectedZcashmainnetBenchmarkAndroidTest -PIS_SIGN_RELEASE_BUILD_WITH_DEBUG_KEY=true` or `./gradlew connectedZcashtestnetBenchmarkAndroidTest -PIS_SIGN_RELEASE_BUILD_WITH_DEBUG_KEY=true` and analyze results with Android Studio's Profiler or [Perfetto](https://ui.perfetto.dev/) tool, as described in this Android [documentation](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview#access-trace).

**Note**: We've enabled benchmarking also for emulators, although it's always better to run the tests on a real physical device. Emulator benchmark improvements might not carry over to a real user's experience (or even regress real device performance).
