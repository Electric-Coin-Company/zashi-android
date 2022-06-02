Note: To be able to fully test the QR Scan screen and its logic, we recommend you to test it on real Android devices, which have hardware camera accessories. Although there is a way on how to test the scanning functionalities on an Android emulator too. See the next section on how to set up a QR code for the emulated camera scene.

# Test Prerequisites
- Check you have a wallet configured in the Zcash wallet app
- Open Zcash wallet app in the system Settings app. Visit the permissions screen, then select Camera and make sure that you have the Camera permission denied.
- Prepare at least two Zcash wallet addresses - one valid and one invalid. A valid address for Testnet can be `tmEjY6KfCryQhJ1hKSGiA7p8EeVggpvN78r`. The invalid one can be its modification.

# Android emulator setup (optional)
This section is optional and is required only if you'd like to test on an Android emulator device.
1. Follow these [steps](https://developer.android.com/studio/install) to download and install Android studio
2. And these [steps](https://developer.android.com/studio/run/managing-avds#createavd) help you set up an Android emulator
3. Then you'll need to create a valid Zcash address QR code image. It can be done, for example, with the [QR Code Generator](https://www.qr-code-generator.com/) tool.
4. Download the image
5. Start the emulator 
6. Click on More in the emulator panel to open the Extended controls window 
7. Click on Camera 
8. Click Add Image in the Wall section 
9. Select your QR code image and close the window
10. Once you're in the QR Scan screen with the virtual camera opened, see these [instructions](https://developers.google.com/ar/develop/java/emulator#control_the_virtual_scene) on how to move in virtual scene.
11. The last step is to let the scanner read your QR code to be able to move to the smaller room (behind the dog), which will have the code displayed on the room wall. 

# Check Camera permission allow functionality
1. Open QR Scan screen by QR code icon from the Home screen
2. Camera permission dialog should be prompt
3. Grant camera permission with Allow button (or its modifications)
4. Camera view and its square frame should appear

# Check Camera permission deny functionality
1. Open QR Scan screen by QR code icon from the Home screen
2. Camera permission dialog should be prompt
3. Deny camera permission with Deny button (or its modifications)
4. Camera view and its square frame shouldn't be visible. The screen should be black. Also, Open system Settings app button on the bottom side of the screen should be visible now.
5. Hit the button.
6. System Settings app should open on the Zcash wallet app screen.

# Scan valid QR code
1. Grant the Camera permission with one of the previous procedures
2. Create QR code from the valid Zcash wallet address. You can use, for example, the [QR Code Generator](https://www.qr-code-generator.com/) tool.
3. Scan the created QR code
4. The code should be scanned and validated
5. App should then close the QR Scan screen and navigate to another screen to proceed with the scanned result

# Scan invalid QR code
1. Grant the Camera permission with one of the previous procedures
2. Create QR code from the valid Zcash wallet address. You can use, for example, the [QR Code Generator](https://www.qr-code-generator.com/) tool.
3. Scan the created QR code
4. The code should be scanned but not validated
5. The app UI should not be changed and the Camera view should be still available for scanning another codes