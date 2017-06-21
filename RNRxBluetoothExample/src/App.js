import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
} from 'react-native';
import RNRxBluetooth from 'rnrxbluetooth';
import _ from 'lodash';
import debug from 'debug';

import DeviceList from './DeviceList';
import Button from './Button';

export default class RNRxBluetoothExample extends Component {

  constructor() {
    super();
    this.debug = debug('RNRxBluetoothExample');
    this.debug.enabled = true;
    this.state = {
      discoveryStarted: false,
      discoveredDevices: [],
      connectedDevice: undefined
    };

    RNRxBluetooth.on('discoveryStart', () => {
      this.debug('discovery: start');
      this.setState({ discoveryStarted: true });
    });

    RNRxBluetooth.on('discoveryEnd', () => {
      this.debug('discovery: end');
      this.setState({ discoveryStarted: false });
    });

    RNRxBluetooth.on('device', (device) => {
      const { address, name } = device;
      this.debug(`discovered: ${address} ${name}`);
      const devices = this.state.discoveredDevices;
      devices.push(device);
      this.setState({ discoveredDevices: _.uniqBy(devices, (dev) => dev.address) });
    });

    RNRxBluetooth.on('connected', (device) => {
      const { address, name } = device;
      this.debug(`connected to: ${address} ${name}`);
      this.setState({ connectedDevice: device });
    });

    RNRxBluetooth.on('data', ({ payload }) => {
      this.debug(`bytes received: ${payload}`);
    });
  }

  startDiscovery() {
    RNRxBluetooth.startDiscovery();
  }

  cancelDiscovery() {
    RNRxBluetooth.cancelDiscovery();
  }

  onDevicePress({ address }) {
    RNRxBluetooth.connect(address);
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
