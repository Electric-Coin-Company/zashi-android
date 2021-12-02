The launcher icon uses different assets on different Android versions, and therefore needs to be explicitly tested.

# API 25 or lower - API 25 and lower use a traditional square icon
1. Install the app
1. Look at the icon on the home screen
1. Verify that a traditional light-themed square icon is displayed

# API 26 or greater - API 26 adds support for adaptive icons
1. Install the app
1. Look at the app icon on the home screen
1. Verify that a round icon is displayed (most emulator images use round icons)

# API 29 or greater - API 29 adds support for dark mode
1. Set the system to light theme
1. Install the app
1. Look at the app icon on the home screen
1. Verify that a round icon is displayed (most emulator images use round icons)
1. Verify that the icon has a light grey background
1. Set the system theme to dark theme
1. Look at the app icon on the home screen
1. Verify that a round icon is displayed (most emulator images use round icons)
1. Verify that the icon has a dark blue background (Note: it may require rebooting or opening an app and returning to the home screen for the home screen to reset its cache of app icons.  This behavior is not a bug in our app, but rather an Android limitation).
