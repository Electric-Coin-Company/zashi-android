Note: To fully test the QR Scan screen and its logic, it's necessary to test it on a real Android device

# Test Prerequisites
- Check you have a wallet configured in the Zcash wallet app
- Open Zcash wallet app in the system Settings app. Visit the permissions screen, then select Camera and make sure that you have the Camera permission denied.
- Prepare at least two Zcash wallet addresses - one valid and one invalid. A valid address for Testnet can be `tmEjY6KfCryQhJ1hKSGiA7p8EeVggpvN78r`. The invalid one can be its modification.

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