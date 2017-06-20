import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableHighlight,
  View,
  ScrollView
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

export default ({ devices, onDevicePress }) =>
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
