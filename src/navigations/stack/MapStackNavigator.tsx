import React from 'react';
import {StyleSheet} from 'react-native';
import {createStackNavigator} from '@react-navigation/stack';
import {mapNavigations} from '@/constants';
import MapHomeScreen from '@/screens/map/MapHomeScreen';

export type MapStackParamList = {
  [mapNavigations.MAP_HOME]: undefined;
};

const Stack = createStackNavigator<MapStackParamList>();

function MapStackNavigator() {
  return (
    <Stack.Navigator
      screenOptions={{
        cardStyle: {
          backgroundColor: 'white',
        },
        headerStyle: {
          shadowColor: 'gray',
          backgroundColor: 'white',
        },
        headerTitleStyle: {
          fontSize: 15,
        },
        headerTintColor: 'black',
      }}>
      <Stack.Screen
        name={mapNavigations.MAP_HOME}
        component={MapHomeScreen}
        options={{
          headerTitle: ' ',
          headerShown: false,
        }}
      />

    </Stack.Navigator>
  );
}

export default MapStackNavigator;