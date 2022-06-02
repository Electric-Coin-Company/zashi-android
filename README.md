# secant-android-wallet
_Note: As of September 2021, this is a brand new project.  Since it is being developed in the open from the beginning, initially this project will not be fully functional.  Some of the documentation below will be aspirational until the implementation is further along.  During this initial stage of development, the older [Zcash Android Wallet](https://github.com/zcash/zcash-android-wallet) may be a more helpful sample._

This is a sample implementation of a Zcash wallet for Android leveraging the [Zcash Android SDK](https://github.com/zcash/zcash-android-wallet-sdk).  The goal is to exercise the SDK and related Zcash libraries, as well as demonstrate how the SDK works.

While we aim to continue improving this sample, it is not an official product.  We open sourced it as a resource to make wallet development easier for the Zcash ecosystem.

# Getting Started
If you'd like to compile this application from source, please see our [Setup Documentation](docs/Setup.md) to get started.

# Reporting an issue
If you wish to report a security issue, please follow our [Responsible Disclosure guidelines](https://github.com/zcash/ZcashLightClientKit/blob/master/responsible_disclosure.md).  See the [Wallet App Threat Model](https://zcash.readthedocs.io/en/latest/rtd_pages/wallet_threat_model.html) for more information about the security and privacy limitations of the wallet.

If you'd like to report a technical issue or feature request for the Android Wallet, please file a [GitHub issue](https://github.com/zcash/secant-android-wallet/issues/new/choose).

General Zcash questions and/or support requests and are best directed to either:
 * [Zcash Forum](https://forum.zcashcommunity.com/)
 * [Discord Community](https://discord.io/zcash-community)

# Contributing
Contributions are very much welcomed!  Please read our [Contributing Guidelines](docs/CONTRIBUTING.md) to learn about our process.

# Forking
If you plan to fork the project to create a new app of your own, please make the following changes.  (If you're making a GitHub fork to contribute back to the project, these steps are not necessary.)

1. Change the app name under app/src/main/res/values/strings.xml
1. Change the support email address under ui-lib/src/res/ui/support/values/strings.xml
1. Remove any copyrighted ZCash or Electric Coin Company icons, logos, or assets
    1. ui-lib/src/main/res/common/ - All of the the ic_launcher assets
1. Change the package name
    1. Under [app/build.gradle.kts](app/build.gradle.kts), change the package name of the application
1. Optional
    1. Configure secrets for [Continuous Integration](docs/CI.md).

# Known Issues

1. During builds, a warning will be printed that says "Unable to detect AGP versions for included builds. All projects in the build should use the same AGP version."  This can be safely ignored.  The version under build-conventions is the same as the version used elsewhere in the application.
1. When the code coverage Gradle property `IS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED` is enabled, the debug app APK cannot be run.  The coverage flag should therefore only be set when running automated tests.
1. Test coverage for Compose code will be low, due to [known limitations](https://github.com/jacoco/jacoco/issues/1208) in the interaction between Compose and Jacoco.
1. Adding the `espresso-contrib` dependency will cause builds to fail, due to conflicting classes.  This is a [known issue](https://github.com/zcash/zcash-android-wallet-sdk/issues/306) with the Zcash Android SDK.
1. Android Studio will warn about the Gradle checksum.  This is a [known issue](https://github.com/gradle/gradle/issues/9361) and can be safely ignored.
1. [#96](https://github.com/zcash/secant-android-wallet/issues/96) - Release builds print some R8 warnings which can be safely ignored.
1. During app first launch, the following exception starting with `AndroidKeysetManager: keyset not found, will generate a new one` is printed twice.  This exception is not an error, and the code is not being invoked twice.
1. When running instrumentation tests for the app module, this warning will be printed `WARNING: Failed to retrieve additional test outputs from device.
com.android.ddmlib.SyncException: Remote object doesn't exist!` followed by a stacktrace.  This can be safely ignored.