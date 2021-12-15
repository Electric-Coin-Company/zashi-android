# Setup Guide
The app is built with Gradle and can be compiled on macOS, Windows, and Linux.  Development is typically done with the latest stable release of Android Studio.

Tip: On macOS and Linux, Gradle is invoked with `./gradlew`.  On Windows, Gradle is invoked with `gradlew`.  Throughout the documentation, the macOS and Linux syntax is used by default.

Tip: You do NOT need to install Gradle yourself.  Running it from the command line or building the application within Android Studio will download the necessary dependencies automatically.

## Step by Step
To get set up for development, there are several steps that you need to go through.  Going through these steps in order is important, as each step in the progression builds on the prior steps.   These steps are written assuming a fresh development environment.

Start by making sure the command line with Gradle works first, because **all the Android Studio run configurations use Gradle internally.**  The run configurations are not magic—they map directly to command line invocations with different arguments.

1. Install Java
    1. Install JVM 11 or greater on your system.  Our setup has been tested with Java 11-17.  For Windows or Linux, be sure that the `JAVA_HOME` environment variable points to the right Java version.
    1. Android Studio has an embedded JVM, although running Gradle tasks from the command line requires a separate JVM to be installed.  Our Gradle scripts are configured to use toolchains to automatically install the correct JVM version.  _Note: The ktlintFormat task will fail on Apple Silicon unless a Java 11 virtual machine is installed manually._
1. Install Android Studio and the Android SDK
    1. Download the [Android Studio Bumblebee Beta](https://developer.android.com/studio/preview) (we're using the Beta version, due to its improved integration with Jetpack Compose)
    1. Note: Do not open the project in Android Studio yet.  That happens in a subsequent step below.  At this stage, we're just using Android Studio to install the Android SDK
    1. TODO: Fill in step-by-step instructions for setting up a new environment and installing the Android SDK from within Android Studio
1. Check out the code.  _Use the command line (instead of Android Studio) to check out the code. This will ensure that your command line environment is set up correctly and avoids a few pitfalls with trying to use Android Studio directly.  Android Studio's built-in git client is not as robust as standalone clients_
    1. To check out a git repo from GitHub, there are three authentication methods: SSH, HTTPS, and GitHub API.  We recommend SSH.
    1. Create a new SSH key, following [GitHub's instructions](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)
    1. Add the SSH key under [GitHub account settings](https://github.com/settings/keys)
    1. Clone repo in a terminal on your computer `git clone git@github.com:zcash/secant-android-wallet.git`
1. Compile from the command line
    1. Navigate to the repo checkout in a terminal
    1. Compile the application with the gradle command `./gradlew assemble`
1. Compile from Android Studio
    1. Open Android Studio
    1. From within Android Studio, choose to open an existing project and navigate to the root of the checked out repo.  Point Android Studio to the root of the git repo as (do not point it to the `app` module, as that is just a subset of the project and cannot be opened by itself)
        1. Note: When first opening the project, Android Studio will warn that Gradle checksums are not fully supported.  Choose the "Use checksum" option.  This is a security feature that we have explicitly enabled.
        1. Shortly after opening the project, Android Studio may prompt about updating the Android Gradle Plugin.  DO NOT DO THIS.  If you do so, the build will fail because the project also has dependency locking enabled as a security feature.  To learn more, see [Build%20Integrity.md](Build integrity.md)
        1. Android Studio may prompt about updating the Kotlin plugin.  Do this.  Our application often uses a newer version of Kotlin than is bundled with Android Studio.
    1. After Android Studio finishes syncing with Gradle, look for the green "play" run button in the toolbar.  To the left of it, choose the "app" run configuration under the dropdown menu.  Then hit the run button

## Troubleshooting
1. Try running from the command line instead of Android Studio, to rule out Android Studio issues.  If it works from the command line, try this step to reset Android Studio
   1. Quit Android Studio
   2. Deleting the invisible `.idea` in the root directory of the project
   3. Relaunch Android Studio
2. Clean the individual Gradle project by running `./gradlew clean` which will purge local build outputs.
3. Run Gradle with the argument `--rerun-tasks` which will effectively disable the build cache by re-running tasks and repopulating the cache.  E.g. `./gradlew assemble --rerun-tasks`
4. Reboot your computer, which will ensure that Gradle and Kotlin daemons are completely killed and relaunched
5. Delete the global Gradle cache under `~/.gradle/caches`
6. If adding a new dependency or updating a dependency, a warning that a dependency cannot be found may indicate the Maven repository restrictions need adjusting

## Gradle Tasks
A variety of Gradle tasks are set up within the project, and these tasks are also accessible in Android Studio as run configurations.
 * `assemble` - Compiles the application but does not deploy it
 * `assembleAndroidTest` - Compiles the application and tests, but does not deploy the application or run the tests
 * `detektAll` - Performs static analysis with Detekt
 * `ktlint` - Performs code formatting checks with ktlint
 * `lint` - Performs static analysis with Android lint
 * `dependencyUpdates` - Checks for available dependency updates

## Gradle Properties
A variety of Gradle properties can be used to configure the build.

### Debug Signing
By default, the application is signed by the developers automatically generated debug signing key.  In a team of developers, it may be advantageous to share a debug key so that debug builds can access key-restricted services such as Firebase or Google Maps.  For such a setup, the path to a shared debug signing key can be set with the property `ZCASH_DEBUG_KEYSTORE_PATH`.

### Release Signing
To enable release signing, a release keystore needs to be provided during the build.  This can be injected securely by setting the following Gradle properties.
* ZCASH_RELEASE_KEYSTORE_PATH
* ZCASH_RELEASE_KEYSTORE_PASSWORD
* ZCASH_RELEASE_KEY_ALIAS
* ZCASH_RELEASE_KEY_ALIAS_PASSWORD

On a developer machine, these might be set under the user's global properties (e.g. `~/.gradle/gradle.properties` on macOS and Linux).  On a continuous integration machine, these can also be set using environment variables with the prefix `ORG_GRADLE_PROJECT_` (e.g. `ORG_GRADLE_PROJECT_ZCASH_RELEASE_KEYSTORE_PATH`).  DO NOT set these in the gradle.properties inside the Git repository, as this will leak your keystore password.
