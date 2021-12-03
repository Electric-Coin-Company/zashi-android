Android has different splash screen behavior starting with Android 12 (API 31), so the splash screen must be tested on both older and newer versions of Android.

Note that splash screens are only displayed when launched from the home screen.  They are not displayed when the app is installed from Android Studio.

Note that if the splash screen appears too quickly, the `SPLASH_SCREEN_DELAY` can be set to keep the splash screen visible for longer.

# Android 11 (API 30) or lower
1. Install the app
1. Set the system to light theme (Android 10 introduced light/dark theme)
1. Launch the app
1. Verify the splash screen appears in the light theme
1. Press back (NOT HOME) to leave the app
1. For Android 10 and 11, set the system theme to dark theme
1. Launch the app
1. Verify the splash screen appears in the dark theme

# Android 12 (API 31) or greater
1. Repeat the tests from above on Android 12 (API 31) or greater, verifying the splash screen looks effectively the same