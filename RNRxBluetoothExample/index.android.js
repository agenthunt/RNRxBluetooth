/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  TouchableOpacity,
  TouchableHighlight,
  View,
  ScrollView
} from 'react-native';
import _ from 'lodash';

const DeviceList = ({ devices, onDevicePress }) =>
  <ScrollView style={styles.container}>
    <View style={styles.listContainer}>
      {devices.map((device, i) => {
        return (
          <TouchableHighlight
            underlayColor='#DDDDDD'
            key={`${device.address}`}
            style={styles.listItem} onPress={() => onDevicePress(device)}>
              <View style={{ justifyContent: 'space-between', flexDirection: 'row', alignItems: 'center' }}>
                <Text style={{ fontWeight: 'bold' }}>{device.name}</Text>
                <Text>{`<${device.address}>`}</Text>
              </View>
          </TouchableHighlight>
        )
      })}
    </View>
  </ScrollView>

const Button = ({ title, onPress, style, textStyle }) =>
  <TouchableOpacity style={[ styles.button, style ]} onPress={onPress}>
    <Text style={[ styles.buttonText, textStyle ]}>{title.toUpperCase()}</Text>
  </TouchableOpacity>

import RNRxBluetooth from 'rnrxbluetooth';

export default class RNRxBluetoothExample extends Component {

  constructor() {
    super();
    this.state = {
      discoveryStarted: false,
      discoveredDevices: []
    }

    RNRxBluetooth.on('discoveryStart', () => {
      console.log('discovery: start');
      this.setState({ discoveryStarted: true });
    });

    RNRxBluetooth.on('discoveryEnd', () => {
      console.log('discovery: end');
      this.setState({ discoveryStarted: false });
    });

    RNRxBluetooth.on('device', (device) => {
      console.log('device discovered:', device.address);
      const devices = this.state.discoveredDevices;
      devices.push(device);
      this.setState({ discoveredDevices: _.uniqBy(devices, (dev) => dev.address) });
    })
  }

  startDiscovery() {
    RNRxBluetooth.startDiscovery();
  }

  cancelDiscovery() {
    RNRxBluetooth.cancelDiscovery();
  }

  onDevicePress(device) {
    //TODO : connect !
    console.log('device pressed' + device.address);
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
  topBar: {
    height: 56,
    paddingHorizontal: 16,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center' ,
    elevation: 6,
    backgroundColor: '#7B1FA2'
  },
  heading: {
    fontWeight: 'bold',
    fontSize: 16,
    alignSelf: 'center',
    color: '#FFFFFF'
  },
  enableInfoWrapper: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  tab: {
    alignItems: 'center',
    flex: 0.5,
    height: 56,
    justifyContent: 'center',
    borderBottomWidth: 6,
    borderColor: 'transparent'
  },
  connectionInfoWrapper: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 25
  },
  connectionInfo: {
    fontWeight: 'bold',
    alignSelf: 'center',
    fontSize: 18,
    marginVertical: 10,
    color: '#238923'
  },
  listContainer: {
    borderColor: '#ccc',
    borderTopWidth: 0.5
  },
  listItem: {
    flex: 1,
    height: 48,
    paddingHorizontal: 16,
    borderColor: '#ccc',
    borderBottomWidth: 0.5,
    justifyContent: 'center'
  },
  fixedFooter: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    borderTopWidth: 1,
    borderTopColor: '#ddd'
  },
  button: {
    height: 36,
    margin: 5,
    paddingHorizontal: 16,
    alignItems: 'center',
    justifyContent: 'center'
  },
  buttonText: {
    color: '#7B1FA2',
    fontWeight: 'bold',
    fontSize: 14
  },
  buttonRaised: {
    backgroundColor: '#7B1FA2',
    borderRadius: 2,
    elevation: 2
  }
})

AppRegistry.registerComponent('RNRxBluetoothExample', () => RNRxBluetoothExample);
