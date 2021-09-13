# Setup Guide
## Gradle
The app is built with Gradle and can be compiled on macOS, Windows, and Linux.

Tip: On macOS and Linux, Gradle is invoked with `./gradlew`.  On Windows, Gradle is invoked with `gradlew`.  Throughout the documentation, the macOS and Linux syntax is used by default.

Tip: You do NOT need to install Gradle yourself.  Running it from the command line or building the application within Android Studio will download the necessary dependencies automatically.

## Android Studio
Development is typically done with the latest stable release of Android Studio.

## Step by Step
To get set up for development, there are several steps that you need to go through.  Going through these steps in order is important, as each step in the progression builds on the prior steps.

Start by making sure the command line with Gradle works first, because **all the Android Studio run configurations use Gradle internally.**  The run configurations are not magic—they map directly to command line invocations with different arguments.

When troubleshooting, fall back to trying the command line.  Android Studio is a large and complex application, although under the hood it uses Gradle.  Sometimes Android Studio will have a bug or failure that is bypassed by using the Gradle command line directly.

1. Install Java
    1. Java 16 is currently recommended. Java 11 is the minimum requirement for Android Studio.
    1. To simplify installation, use [Oracle's JDK](https://www.oracle.com/java/technologies/javase-jdk15-downloads.html) installer that will place the Java installation in the right place
1. Install Android Studio and the Android SDK
    1. Download the [stable release of Android Studio](https://developer.android.com/studio#downloads)
    1. TODO: Fill in step-by-step instructions for setting up a new environment and installing the Android SDK from within Android Studio.
1. Check out the code.  _Use the command line (instead of Android Studio) to check out the code. This will ensure that your command line environment is set up correctly and avoids a few pitfalls with trying to use Android Studio directly.  Android Studio's built-in git client is not as robust as standalone clients_
1. Compile from the command line
    1. Navigate to the repo checkout in a terminal
    1. Compile the application with the gradle command `./gradlew assemble`
1. Compile from Android Studio
    1. Open Android Studio
    1. From within Android Studio, choose to open an existing project and navigate to the checked out repo
    1. After Android Studio finishes syncing with Gradle, look for the green "play" run button in the toolbar.  To the left of it, choose the "App" run configuration under the dropdown menu.  Then hit the run button

# Troubleshooting
Sometimes during upgrades to Gradle or Kotlin, corruption or weird issues can happen with a build.  Try troubleshooting with the following steps:

1. Try running from the command line instead of Android Studio, to rule out Android Studio issues.  If it works from the command line, try this step to reset Android Studio
    1. Quit Android Studio
    2. Deleting the invisible `.idea` in the root directory of the project
    3. Relaunch Android Studio
2. Clean the individual Gradle project by running `./gradlew clean` which will purge local build outputs.
3. Run Gradle with the flag `--rerun-tasks` which will effectively disable the build cache by re-running tasks and repopulating the cache.  E.g. `./gradlew assemble --rerun-tasks`
4. Reboot your computer, which will ensure that Gradle and Kotlin daemons are completely killed and relaunched
5. Delete the global Gradle cache under `~/.gradle/caches`