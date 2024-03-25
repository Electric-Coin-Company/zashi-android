Note: Contact Support will fail and display an error message on some devices without an app to handle email, such as an 
Android TV device.

# Check Support Email Contents
1. If using a test device or emulator, be sure to configure a default email app.  For example, try opening the Gmail app and confirm that it shows your inbox.
1. Clear the app's data.  This ensures no crash reports exist on external storage
1. Open the Zcash app
1. Navigate to Profile
1. Navigate to Support
1. Type a message
1. Choose send
1. Choose OK
1. Verify that the email app opens with a pre-filled message.  The email subject should be "Zcash", the recipient should be the correct support email address, and the message body should include the message typed above, along with information about the user's current setup.

# Verify Support Screen Closes After Send
1. If using a test device or emulator, be sure to configure a default email app.  For example, try opening the Gmail app and confirm that it shows your inbox.
1. Open the Zcash app
1. Navigate to Profile
1. Navigate to Support
1. Type a message
1. Choose send
1. Choose OK
1. After the email app opens, task switch back to the Zcash app
1. Verify that you're returned to the Profile screen (specifically confirm the Support screen with the confirmation dialog is no longer on the screen)

# With Crashes
1. If using a test device or emulator, be sure to configure a default email app.  For example, try opening the Gmail app and confirm that it shows your inbox.
1. Install a debug build of the app
1. From the Home screen, choose Throw Uncaught Exception from the debug menu
1. Repeat that at least 6 times
1. Open the Zcash app
1. Navigate to Profile
1. Navigate to Support
1. Type a message
1. Choose send
1. Choose OK
1. Verify that the email app opens with a pre-filled message.  Specifically look at the Exceptions section, verifying that it contains 5 entries (we limit to 5 to keep the email from getting too long).  Note that the number of reported exceptions is set via `CrashInfo.MAX_EXCEPTIONS_TO_REPORT` and could be changed over time.