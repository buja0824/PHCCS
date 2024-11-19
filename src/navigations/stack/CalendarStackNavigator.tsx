import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import CalendarHomeScreen from '@/screens/calendar/CalendarHomeScreen';
import {colors} from '@/constants';
import Ionicons from 'react-native-vector-icons/Ionicons';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import {TouchableOpacity} from 'react-native';
import {CompoundOption} from '@/components/common/CompoundOption';

export type CalendarStackParamList = {
  CalendarHome: {
    showDeleteConfirm?: boolean;
  };
};

const Stack = createStackNavigator<CalendarStackParamList>();

function CalendarStackNavigator() {
  return (
    <Stack.Navigator
      screenOptions={({navigation}) => ({
        headerLeft: () => (
          <TouchableOpacity 
            onPress={() => navigation.openDrawer()} 
            style={{marginLeft: 10}}
          >
            <Ionicons name="menu" size={25} color={colors.light.GRAY_700} />
          </TouchableOpacity>
        ),
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
      })}>
      <Stack.Screen
        name="CalendarHome"
        component={CalendarHomeScreen}
        options={({navigation}) => ({
          title: '캘린더',
          headerRight: () => (
            <TouchableOpacity 
              onPress={() => navigation.setParams({ showDeleteConfirm: true })}
              style={{marginRight: 16}}
            >
              <MaterialIcons name="delete-outline" size={22} color={colors.light.RED_700} />
            </TouchableOpacity>
          ),
        })}
      />
    </Stack.Navigator>
  );
}

export default CalendarStackNavigator; 