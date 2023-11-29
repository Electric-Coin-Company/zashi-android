# Sideloading
This documentation describes how to sideload a Debug or Universal release APK created by the project Continuous 
Integration. It also provides details on how to install an Android emulator on a desktop machine for non-Android users.

1. Obtain binary
    1. Look under the [GitHub Actions tab](https://github.com/Electric-Coin-Company/zashi-android/actions). Every 
       pull request and merge to the Main branch will trigger a workflow that generates builds of the app. The 
       workflows are called _Pull Request_ and _Deploy_.
    1. Click on a successful workflow
    1. Scroll down the workflow results page to find an attached build output called _Binaries_
    1. Download the Binaries file
    1. Look for either the Universal or Debug APK in the flavor that you'd like to install. It should be under 
       `app/build/outputs/`
1. Install binary
   1. If using a **physical device**, run the terminal command `adb install -r $PATH_TO_APK` (see more about [ADB 
   commands](https://developer.android.com/tools/adb)). Alternatively, if you have the Android Studio installed, you 
      can drag-and-drop the selected APK file to the physical device screen shared in the Running devices panel. 
      Then, open the installed app from the device launcher.
   1. If using a **virtual device**, you can drag-and-drop the APK file onto the virtual device screen as well. See the 
      next section on how to install an Android emulator on a desktop machine.

## Install emulator
Besides third-party, often paid solutions, there are two ways to run an Android emulator on a desktop. Note that the 
following instructions describe the installation process on a Unix-based machine, but after a slight adjustment, it can
also be used for a Windows-based machine.

1. Install [Android Studio](https://developer.android.com/studio) using the instructions in the first steps of
[Setup](Setup.md) documentation. The Android studio installation requires about 8 GB RAM or more and 8 GB of available 
   disk space (IDE + Android SDK + Android Emulator).
1. The second option is to install the Android SDK tools only, which requires less disk space and is used 
   within the machine's terminal. Remember that this approach requires some manual steps, and you might need to 
   adjust the commands based on the specific versions and configurations you choose. 
   - Tip: On Unix-based OS, SdkManager and the other tools are invoked with `./sdkmanager`. On Windows, SdkManager 
     is invoked with `sdkmanager`. Throughout the documentation, the macOS and Linux syntax is used by  default.
   1. Download Android **cmdline-tools**
      - If you haven't already, download the Android cmdline-tools from the official 
      [website](https://developer.android.com/studio), section _Command Line Tools_.
      - Extract the downloaded archive to a location of your choice. 
      - In the unzipped cmdline-tools directory, create a subdirectory called `latest`. 
      - Move the original `cmdline-tools` directory contents, including the `lib` directory, `bin` directory,
        `NOTICE.txt` file, and `source.properties` file, into the newly created `latest` directory.
      - Move the updated `cmdline-tools` directory into a new directory, such as `android_sdk`. This new directory is 
        your Android SDK directory now.
      - Open the Terminal cmdline app and navigate to the new `android_sdk` directory. You can now use the command-line 
        tools from this location.
   1. Install Android **platform-tools** using the new `sdkmanager` with accepting all the necessary licence agreements
      ```
      ./cmdline-tools/latest/bin/sdkmanager "platform-tools"
      ```
   2. Install Android SDK Packages using the new `sdkmanager`
      - Run the following command to install the necessary SDK packages. Replace `<version>` with the desired 
        Android version (e.g., `34`), `<image-type>` with the system image type (e.g., `google_apis`), and `<abi>` with 
        the desired ABI (e.g., `arm64-v8a`).
      ```
      ./cmdline-tools/latest/bin/sdkmanager "platforms;android-<version>" "system-images;android-<version>;<image-type>;<abi>"
      ```
      - Example of such a command:
      ```
      ./cmdline-tools/latest/bin/sdkmanager "platforms;android-34" "system-images;android-34;google_apis;arm64-v8a"
      ```
   1. Create an AVD (Android Virtual Device)
      - Run the following command to create an AVD. Replace `<avd-name>` with the desired name for 
        your AVD (e.g., `pixel7_api34`), `<image-type>`, `<verison>` and `<abi>` with the previously selected. Then, 
        also replace `<sd-card-size>` with the desired size for the SD card (e.g., `1024M`), and `<device>` with the 
        desired device (e.g., `pixel 7`).
      ```
      ./cmdline-tools/latest/bin/avdmanager create avd -n <avd-name> -k "system-images;android-<version>;<image-type>;<abi>" -c <sd-card-size> -d <device> --abi <abi> --force
      ```
      - Example of such a command:
      ```
      ./cmdline-tools/latest/bin/avdmanager create avd -n pixel7_api34 -k "system-images;android-34;google_apis;arm64-v8a" -c 1024M -d pixel_7 --abi "arm64-v8a" --force
      ```
   1. Run the Emulator
      - Start the emulator with the following command. Replace `<avd-name>` with the name you gave to your AVD:
      ```
      ./emulator/emulator -avd <avd-name>
      ```
       - Example of such a command:
      ```
      ./emulator/emulator -avd pixel7_api34
      ```
      - Now, you should have the Android emulator running on your machine without installing the Android Studio, and 
        you can finish the app installation from the previous section.

### Build variants notes
- Apps can be distributed in two different formats: Application Package (APK) and Android App Bundle (AAB).  AAB is
  uploaded to Google Play, and allows the store to deliver device-specific slices (e.g. CPU architecture, screen
  size, etc.) for smaller downloads.  APK is the format for sideloading.  APK files are the original format from
  Android 1.0, and can be generated directly from a Gradle build.  AAB files are a newer format, and APK files can
  also be derived from AAB.  A "universal" APK is one that was derived from an AAB without any slicing.  We use
  "universal" APKs for testing of release builds, as they are processed through bundletool (which has introduced
  bugs in the past) and therefore somewhat closer to what would be delivered to end user devices.
- Android apps must be digitally signed.  The signing key is critical to app sandbox security (preventing other apps
  from reading our app's data). We have multiple signing configurations:
   - If you build from source, your computer will generate a random debug signing key.  This key will be consistent
     for multiple builds, allowing you to keep re-deploying changes of the app.  But if you connect your physical
     Android device to a different computer, the debug key will be different and therefore the app will need to be
     uninstalled/reinstalled to update it.
   - Debug builds on the CI server always have a new randomly generated signing key for each build.  This means each
     time you sideload a CI generated debug build, uninstall/reinstall will be necessary.
   - Release builds from a Pull Request workflow always have a new randomly generated signing key for each build.  
     This means each time you sideload a CI generated debug build, uninstall/reinstall will be necessary.
   - Release builds from a Deploy workflow always are signed with the "upload" keystore.  The upload keystore is
     consistent and is the signing key for uploading to Google Play.  Google Play will then re-sign the app with the
     signing keystore when delivering the app to end users.  Therefore, moving between a release build downloaded
     from Google Play versus one sideloaded from a Deploy workflow will require uninstall/reinstall.