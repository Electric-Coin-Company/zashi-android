Note: To be able to fully test the QR Scan screen and its logic, we recommend you to test it on real Android devices, which have hardware camera accessories. Although there is a way on how to test the scanning functionalities on an Android emulator too. See the next section on how to set up a QR code for the emulated camera scene.

# Test Prerequisites
- Prepare at least two Zcash wallet addresses - one valid and one invalid. A valid address for Testnet can be `tmEjY6KfCryQhJ1hKSGiA7p8EeVggpvN78r`. The invalid one can be its modification.
- Check you have a wallet configured in the Zcash wallet app
- Open Zcash wallet app in the system Settings app. Visit the permissions screen, then select Camera and make sure that you have the Camera permission denied.
- The previous step of resetting the Camera permission needs to be repeated before each following subtests by one of these ways:
  - Reset the wallet app data from system Settings app to reset the Camera permission
  - Reinstall the wallet app
  - Change the Camera permission settings by switching between available options (the options may differ on different Android SDK versions).

# Android emulator setup (optional)
This section is optional and is required only if you'd like to test on an Android emulator device.
1. Follow these [steps](https://developer.android.com/studio/install) to download and install Android studio
1. And these [steps](https://developer.android.com/studio/run/managing-avds#createavd) help you set up an Android emulator
1. Then you'll need to create a valid Zcash address QR code image. It can be done, for example, with the [QR Code Generator](https://www.qr-code-generator.com/) tool.
1. Download the image
1. Start the emulator 
1. Click on More in the emulator panel to open the Extended controls window 
1. Click on Camera 
1. Click Add Image in the Wall section 
1. Select your QR code image and close the window
1. Once you're in the QR Scan screen with the virtual camera opened, see these [instructions](https://developers.google.com/ar/develop/java/emulator#control_the_virtual_scene) on how to move in virtual scene.
1. The last step is to let the scanner read your QR code to be able to move to the smaller room (behind the dog), which will have the code displayed on the room wall.

# Check Camera permission allow functionality
1. Open QR Scan screen by QR code icon from the Home screen
1. Camera permission dialog should be prompt
1. Grant camera permission with Allow button (or its modifications)
1. Camera view and its square frame should appear

# Check Camera permission deny functionality
1. Open QR Scan screen by QR code icon from the Home screen
1. Camera permission dialog should be prompt
1. Deny camera permission with Deny button (or its modifications)
1. Camera view and its square frame shouldn't be visible. The screen should be black. Also, Open system Settings app button on the bottom side of the screen should be visible now.
1. Hit the button.
1. System Settings app should open on the Zcash wallet app screen.

# Check Camera permission dismiss functionality
1. Open QR Scan screen by QR code icon from the Home screen
1. Camera permission dialog should be prompt
1. Dismiss the Camera permission by back navigation or by touching outside the Camera permission popup
1. Rotate the device to trigger a configuration change (make sure the screen rotation is enabled on the device)
1. Ensure that the Camera permission is shown again
1. Leave the QR Scan screen by back navigation; you're on Home screen now
1. Ensure that Camera permission is not shown above the Home screen

# Scan valid QR code
1. Grant the Camera permission with one of the previous procedures
1. Create QR code from the valid Zcash wallet address. You can use, for example, the [QR Code Generator](https://www.qr-code-generator.com/) tool.
1. Scan the created QR code
1. The code should be scanned and validated
1. App should then close the QR Scan screen and navigate to another screen to proceed with the scanned result

# Scan invalid QR code
1. Grant the Camera permission with one of the previous procedures
1. Create QR code from the valid Zcash wallet address. You can use, for example, the [QR Code Generator](https://www.qr-code-generator.com/) tool.
1. Scan the created QR code
1. The code should be scanned but not validated (error message is displayed).
1. The app UI should not be changed and the Camera view should be still available for scanning another codes