# Configure keep screen on setting - Because sync can take a long time, the app has a feature to keep the screen on until sync completes.  The functionality to keep the screen on will apply to any screen while the app is open and syncing is actively in progress
1. Change the systemwide screen timeout in the Android Settings to something short, e.g. 15 seconds
1. Install the app
1. Launch the app
1. Get to the app's settings
1. Enable the "keep screen on while syncing" option
1. Assuming that sync will take a long time, leave the app open and do not touch the screen for more than the systemwide screen timeout duration
1. Verify that the screen does not turn off
1. Go to the settings and disable the "Keep screen on while syncing" option
1. Keeping the app on the screen, leave the device alone for more than the systemwide screen timeout
1. Verify that the screen does turn off
1. Wake the device
1. Turn the "Keep screen on while syncing" option back on
1. Return to the home screen
1. Leave the device alone until sync completes (this may take hours)
1. Verify when you return that the screen is off (the screen should turn off within the systemwide screen timeout after syncing completes)

# Disable background syncing
1. Install a debug build of the app and connect the device to a system with the Android developer tools installed
1. Perform a fresh install of the app
1. Go through the onboarding to get to the home screen
1. In the developer tools (App Inspection -> Background Task Inspector), verify that a periodic WorkManager job is scheduled or running
1. Go into the app's settings and disable the background sync option
1. In the developer tools, verify that no periodic WorkManager job is scheduled or running (e.g. it may be cancelled)
1. Go into the app's settings and re-enable the background sync option
1. In the developer tools, verify that a periodic WorkManager job is scheduled