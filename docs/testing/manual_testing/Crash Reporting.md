The application will log crashes to external storage and can also include some information about these when a user contacts us for support.

# Crashes Reported to External Storage
1. Uninstall the app, to clear external storage
2. Install a debug build of the app
3. Get past the onboarding to reach the Home screen
4. Under the debug menu, choose Report Caught Exception
5. Look under the app's external storage directory `/sdcard/Android/data/co.electroiccoin.zcash/files/log/exception/`
6. Confirm that a new exception file exists in this directory
7. Repeat this with the "Throw Uncaught Exception" under the debug menu

# Crashes Reported in Contact Support
1. See the Contact Support test cases