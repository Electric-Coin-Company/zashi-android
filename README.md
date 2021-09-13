# secant-android-wallet
_Note: As of September 2021, this is a brand new project.  Since it is being developed in the open from the beginning, initially this project will not be fully functional.  Some of the documentation below will be aspirational until the implementation is further along.  During this initial stage of development, the older [Zcash Android Wallet](https://github.com/zcash/zcash-android-wallet) may be a more helpful sample._

This is a sample implementation of a Zcash wallet for Android leveraging the [Zcash Android SDK](https://github.com/zcash/zcash-android-wallet-sdk).  The goal is to exercise the SDK and related Zcash libraries, as well as demonstrate how the SDK works.

While we aim to continue improving this sample, it is not an official product.  We open sourced it as a resource to make wallet development easier for the Zcash ecosystem.

# Getting Started
If you'd like to compile this application from source, please see our [Setup Documentation](docs/Setup.md) to get started.

# Reporting an issue
If you wish to report a security issue, please follow our [Responsible Disclosure guidelines](https://github.com/zcash/ZcashLightClientKit/blob/master/responsible_disclosure.md).  See the [Wallet App Threat Model](https://zcash.readthedocs.io/en/latest/rtd_pages/wallet_threat_model.html) for more information about the security and privacy limitations of the wallet.  There are some known security and privacy limitations:
- Traffic analysis, like in other cryptocurrency wallets, can leak some privacy of the user.
- The wallet requires a trust in the server to display accurate transaction information.

If you'd like to report a technical issue or feature request for the Android Wallet, please file a [GitHub issue](https://github.com/zcash/secant-android-wallet/issues/new/choose).

General Zcash questions and/or support requests and are best directed to either:
 * [Zcash Forum](https://forum.zcashcommunity.com/)
 * [Discord Community](https://discord.io/zcash-community)

# Contributing
Contributions are very much welcomed!  Please read our [Contributing Guidelines](docs/CONTRIBUTING.md) to learn about our process.
