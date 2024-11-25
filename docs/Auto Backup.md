# Android Auto Backup

- The Zashi app uses the Android Auto Backup feature to back up the encrypted address book file securely on the 
  user's Google Drive cloud  

## Android Auto Backup testing

To force the system Android backup logic for testing use the following ADB commands:
```
adb shell bmgr enable true
adb shell bmgr backupnow co.electriccoin.zcash.debug
```
