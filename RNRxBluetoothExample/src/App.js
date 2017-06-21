import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
} from 'react-native';
import RNRxBluetooth from 'rnrxbluetooth';
import _ from 'lodash';

import DeviceList from './DeviceList';
import Button from './Button';

export default class RNRxBluetoothExample extends Component {

  constructor() {
    super();
    this.state = {
      discoveryStarted: false,
      discoveredDevices: [],
      connectedDevice: undefined
    };

    RNRxBluetooth.on('discoveryStart', () => {
      console.log('discovery: start');
      this.setState({ discoveryStarted: true });
    });

    RNRxBluetooth.on('discoveryEnd', () => {
      console.log('discovery: end');
      this.setState({ discoveryStarted: false });
    });

    RNRxBluetooth.on('device', (device) => {
      console.log('device discovered:', device.address, device.name);
      const devices = this.state.discoveredDevices;
      devices.push(device);
      this.setState({ discoveredDevices: _.uniqBy(devices, (dev) => dev.address) });
    });

    RNRxBluetooth.on('connected', (device) => {
      console.log('connected to:', device.address, device.name);
      this.setState({ connectedDevice: device });
    });

    RNRxBluetooth.on('data', (data) => {
      console.log('rec: ' + data.payload);
    });
  }

  startDiscovery() {
    RNRxBluetooth.startDiscovery();
  }

  cancelDiscovery() {
    RNRxBluetooth.cancelDiscovery();
  }

  onDevicePress(device) {
    RNRxBluetooth.connect(device.address);
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          RNRxBluetooth example
        </Text>
        <Button
          textStyle={{ color: '#FFFFFF' }}
            style={styles.buttonRaised}
            title={this.state.discoveryStarted ? 'Cancel Discovery': 'Start Discovery'}
            onPress={this.state.discoveryStarted ? () => this.cancelDiscovery(): () => this.startDiscovery()} />
        <DeviceList
            devices={this.state.discoveredDevices}
            connectedDevice={this.state.connectedDevice}
            onDevicePress={(device) => this.onDevicePress(device)} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 0.9,
    backgroundColor: '#F5FCFF'
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  buttonRaised: {
    backgroundColor: '#7B1FA2',
    borderRadius: 2,
    elevation: 2
  }
});
