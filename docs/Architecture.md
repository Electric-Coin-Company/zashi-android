# Architecture
TODO This is a placeholder for describing the app architecture.
## Gradle
 * Versions are declared in [gradle.properties](../gradle.properties).  There's still enough inconsistency in how versions are handled in Gradle, that this is as close as we can get to a universal system.  A version catalog is used for dependencies and is configured in [settings.gradle.kts](../settings.gradle.kts), but other versions like Gradle Plug-ins, the NDK version, Java version, and Android SDK versions don't fit into the version catalog model and are read directly from the properties
 * Much of the Gradle configuration lives in [build-conventions](../build-conventions/) to prevent repetitive configuration as additional modules are added to the project
 * Build scripts are written in Kotlin, so that a single language is used across build and the app code bases
 * Only Gradle, Google, and JetBrains plug-ins are included in the critical path.  Third party plug-ins can be used, but they're outside the critical path.  For example, the Gradle Versions Plugin could be removed and wouldn't negative impact building, testing, or deploying the app
 
## App
The main entrypoints of the application are:
 * [AppImpl.kt](../app/src/main/java/cash/z/ecc/app/AppImpl.kt) - The root Application object defined in the app module
 * [MainActivity.kt](../ui-lib/src/main/java/cash/z/ecc/ui/MainActivity.kt) - The main Activity, defined in ui-lib.  Note that the Activity is NOT exported.  Instead, the app module defines an activity-alias in the AndroidManifest which is what presents the actual icon on the Android home screen.

## Modules
The logical components of the app are implemented as a number of Gradle modules.

 * app — Compiles all of the modules together into the final application.  This module contains minimal actual code.  Note that the Java package structure for this module is under `cash.z.ecc.app` while the Android package name is `cash.z.ecc`.
 * ui-lib — User interface that the user interacts with.  This contains 99% of the UI code, along with localizations, icons, and other assets.