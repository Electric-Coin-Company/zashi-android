# Check transactions appear in the Transaction History screen

## Test Prerequisites
- Testnet wallet seed phrase mnemonic:
  `board palm case fever fuel above dinosaur caution erode search ignore damage print rare lady agent stereo tomorrow end thank hurry deputy swamp wild`
- And its birthday height: `2379900`
- Install the latest Testnet variant wallet app
- Open the wallet app and for restoring its previous state, use the wallet information above

## Test
- Click on the _Transaction History_ button on the _Home_ screen (an additional scroll down may be needed)
- **Confirm** there are no transactions displayed. There should be only a text saying that the transaction may 
  appear once the sync completes.
- Go back to the _Home_ screen
- **Wait** for a few minutes to sync completes and _Up-to-date_ is displayed under the current balance text 
- Go to the _Transaction History_ screen again
- **Confirm** that there are some transactions displayed. There should be some incoming as well as outgoing 
  transactions displayed.