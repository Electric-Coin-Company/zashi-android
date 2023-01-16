* Debug APK contents — Androidx libraries include extra text files that Android Studio reads during debugging.  We need to makes sure these files are preserved for debug builds.
    1. Create a debug build of the app, e.g. `./gradlew :app:assembleDebug`
    1. View the APK in Android Studio
        1. Build menu
        1. Analyze APK
        1. Open the APK that was built in the first step above
    1. Verify the APK contains various debug properties and versions files in the root directory and under META-INF/

* Release APK contents 
    1. Create a debug build of the app, e.g. `./gradlew :app:assembleRelease`
    1. View the APK in Android Studio
        1. Build menu
        1. Analyze APK
        1. Open the APK that was built in the first step above
    1. Verify the APK contains various debug properties
    * Verify the unzipped directory contains the following:
        * AndroidManifest.xml
        * assets/
        * classes.dex (and perhaps additional classesN.dex)
        * compact_formats.proto
        * darkside.proto
        * google/
        * lib/
        * META-INF/ (which should only contain a few files)
        * res/
        * resources.arsc
        * service.proto

 * Sanity check release app
    1. Create a release build of the app, e.g. `./gradlew :app:bundleRelease :app:packageZcashmainnetReleaseUniversalApk`.  Note these Gradle tasks will create an app bundle, then derive the APK from the app bundle.  This more closely matches how the app would be distributed to users through Google Play.
    1. Connect an Android device with developer mode enabled to your computer
    1. On a computer with the Android developer tools, run `adb logcat`
    1. Install the app on the device, e.g. `adb install -r $pathToApk`
    1. Run the app
    1. Verify
        1. The app launches successfully
        1. Minimal logs from the app are printed to logcat.  Android itself may print logs, but our release build should have logging stripped out
        1. The app is using mainnet
