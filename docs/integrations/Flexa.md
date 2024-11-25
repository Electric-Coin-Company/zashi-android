# Flexa Integration
- Flexa is a digital payment platform that allows merchants to accept various digital assets, including 
  cryptocurrencies, as payment. It provides a seamless and secure way for users to pay with their digital wallets at 
  physical stores.
- The Android Flexa libraries are available on the [GitHub repository](https://github.com/flexa/flexa-android)
- The Android Flexa libraries are currently distributed by [GitHub Releases](https://github.com/flexa/flexa-android/releases). 

# Setup
To bump the Flexa libraries use the following steps:
1. Visit the Flexa GitHub Releases [page](https://github.com/flexa/flexa-android/releases)
1. Download Core and Spent `.aar`(s)
1. Put them into the related folders on path `/maven/com/flexa/`
1. Update the `maven-metadata-local.xml` files to the given new version
1. Update the `.pom`(s) files to the given new version
1. Update `gradle.properties#FLEXA_VERSION` to point the given version 
