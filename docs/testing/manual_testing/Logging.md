# Logging disabled on release builds
1. Create a release build of the app, e.g. `./gradlew assembleRelease`
1. Install the build, e.g. `adb install [path_to_apk]`
1. Start logcat, e.g. `adb logcat`
1. Launch the app
1. Verify that the app does not crash on launch (the app is designed to crash if logs are enabled in release builds)
1. Verify that no logs from the app appear in logcat

