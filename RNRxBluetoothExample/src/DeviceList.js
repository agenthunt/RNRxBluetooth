import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableHighlight,
  View,
  ScrollView,
  Image
} from 'react-native';

const styles = StyleSheet.create({
  container: {
    flex: 0.9,
    backgroundColor: '#F5FCFF'
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
  }
});

export default ({ devices, connectedDevice, onDevicePress }) =>
    <ScrollView style={styles.container}>
      <View style={styles.listContainer}>
        {devices.map((device, i) => {
          return (
            <TouchableHighlight
              underlayColor='#DDDDDD'
              key={`${device.address}`}
              style={styles.listItem} onPress={() => onDevicePress(device)}>
              <View style={{ flexDirection: 'row' }}>
                <View style={{ width: 48, height: 24, opacity: 0.4 }}>
                    {
                      (connectedDevice && connectedDevice.address === device.address)
                        ? <Image style={{ resizeMode: 'contain', width: 24, height: 24 }} source={require('./images/ic_done_black_24dp.png')} />
                        : null
                    }
                </View>
                <View style={{ justifyContent: 'space-between', flexDirection: 'row', alignItems: 'center' }}>
                  <Text style={{ fontWeight: 'bold' }}>{device.name}</Text>
                  <Text>{` <${device.address}>`}</Text>
                </View>
              </View>
            </TouchableHighlight>
          )
        })}
      </View>
    </ScrollView>
