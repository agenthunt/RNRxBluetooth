# RNRxBluetooth

React Native Reactive Bluetooth Library which let you interact with Bluetooth devices [reactively](http://reactivex.io/) from your [React Native](https://facebook.github.io/react-native) application.

This is a simple wrapper around underlying Android and iOS libraries. Current support is:
+ Android, using [RxBluetooth](https://github.com/IvBaranov/RxBluetooth): :white_check_mark:
+ iOS, using [RxBluetoothKit](https://github.com/Polidea/RxBluetoothKit): :x:  *(any help appreciated)*

## Prerequisite

Install React Native following the [offical documentation](https://facebook.github.io/react-native/docs/getting-started.html) and click on **Building Projects with Native Code** to select your development OS.

## Android

### Run the example

From top dir:

+ `cd android`
+ `./gradlew assembleDebug`
+ `cd ../RNRxBluetoothExample`
+ `yarn install` (or npm install)
+ `react-native link`

Then, in one term, launch package manager from top directory `npm start -- --reset-cache`

And in another term, `react-native run-android`

**/!\ Be sure to:**
+ Run the example with a real device as there isn't any simulator supporting bluetooth emulation.
+ Activate the Bluetooth on your device

## See the logs

When on the main page, shake your device and choose `Debug JS Remotely`. This will open the Chrome DevTools in a new Chrome tab and you should see the logs.

# Thanks!

Thanks a lot to [RxBluetooth](https://github.com/IvBaranov/RxBluetooth) and [RxBluetoothKit](https://github.com/Polidea/RxBluetoothKit) maintainers and contributors!
