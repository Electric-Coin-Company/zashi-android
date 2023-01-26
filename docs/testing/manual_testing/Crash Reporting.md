The application will log crashes to external storage and can also include some information about these when a user contacts us for support.

# Crashes Reported to External Storage
1. Uninstall the app, to clear external storage
2. Install a debug build of the app
3. Get past the onboarding to reach the Home screen
4. Under the debug menu, choose Report Caught Exception
5. Look under the app's external storage directory `/sdcard/Android/data/co.electroiccoin.zcash/files/log/exception/`
6. Confirm that a new exception file exists in this directory
7. Repeat this with the "Throw Uncaught Exception" under the debug menu

# Crashes reported to Crashlytics
1. Compile a debug build of the app with Firebase API keys
    1. Download Firebase JSON configuration files from https://console.firebase.google.com and place them in app/src/debug and app/src/release
    1. OR download an APK built by GitHub Actions which has the API keys set up
1. Get past the onboarding to reach the Home screen
1. Under the debug menu, choose Report Caught Exception
1. Log onto the Firebase project and confirm the exception is reported
1. Repeat this with the "Throw Uncaught Exception" under the debug menu

# Crashes Reported in Contact Support
1. See the Contact Support test cases