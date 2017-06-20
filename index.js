const ReactNative = require('react-native');
const { NativeModules, DeviceEventEmitter } = ReactNative;
const RNRxBluetooth = NativeModules.RNRxBluetooth;

/**
 * Listen for available events
 * @param  {String} eventName Name of event one of connectionSuccess, connectionLost, data, rawData
 * @param  {Function} handler Event handler
 */
RNRxBluetooth.on = (eventName, handler) => {
  DeviceEventEmitter.addListener(eventName, handler)
}

/**
 * Stop listening for event
 * @param  {String} eventName Name of event one of connectionSuccess, connectionLost, data, rawData
 * @param  {Function} handler Event handler
 */
RNRxBluetooth.removeListener = (eventName, handler) => {
  DeviceEventEmitter.removeListener(eventName, handler)
}

module.exports = RNRxBluetooth;
