The app has both debug and release builds, as well as testnet and mainnet flavors.  We deploy release mainnet builds to users, but often use debug mainnet or testnet builds for testing.

# Ensure remote config debugging is disabled
1. Download the release APK from CI server or from Google Play (you can also build it locally with `./gradlew assembleRelease` but fetching the version from CI or Google Play will be closer to the version users receive)
1. In Android Studio, go to the Build menu and choose Analyze APK
1. Navigate to the APK file
1. Inspect the AndroidManifest and ensure that no `<receiver>` entry for `IntentConfigurationReceiver` exists

# Ensure logging is stripped

# Ensure application is minified - The application is minified through R8 without obfuscation.  This is especially important to improve runtime performance of the app that we release.
