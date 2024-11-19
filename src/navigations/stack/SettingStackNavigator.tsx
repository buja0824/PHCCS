import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {colors, settingNavigations} from '@/constants';
import SettingHomeScreen from '@/screens/setting/SettingHomeScreen';
import EditProfileScreen from '@/screens/setting/EditProfileScreen';
import EditPasswordScreen from '@/screens/setting/EditPasswordScreen';
import SettingHeaderLeft from '@/components/setting/SettingHeaderLeft';
import DeleteAccountScreen from '@/screens/setting/DeleteAccountScreen';
import useThemeStore from '@/store/useThemeStore';
import {ThemeMode} from '@/types/common';

export type SettingStackParamList = {
  [settingNavigations.SETTING_HOME]: undefined;
  [settingNavigations.EDIT_PROFILE]: undefined;
  [settingNavigations.EDIT_PASSWORD]: undefined;
  [settingNavigations.DELETE_ACCOUNT]: undefined;
};

const Stack = createStackNavigator<SettingStackParamList>();

function SettingStackNavigator() {
  const {theme} = useThemeStore();
  const themeColors = colors[theme as ThemeMode];

  return (
    <Stack.Navigator
      screenOptions={{
        cardStyle: {
          backgroundColor: themeColors.GRAY_100,
        },
        headerStyle: {
          shadowColor: themeColors.GRAY_200,
          backgroundColor: themeColors.WHITE,
        },
        headerTitleStyle: {
          fontSize: 15,
        },
        headerTintColor: themeColors.BLACK,
      }}>
      <Stack.Screen
        name={settingNavigations.SETTING_HOME}
        component={SettingHomeScreen}
        options={({navigation}) => ({
          headerTitle: '설정',
          headerLeft: () => SettingHeaderLeft(navigation),
        })}
      />
      <Stack.Screen
        name={settingNavigations.EDIT_PROFILE}
        component={EditProfileScreen}
        options={{
          headerTitle: '닉네임 수정',
          cardStyle: {
            backgroundColor: themeColors.WHITE,
          },
        }}
      />
      <Stack.Screen
        name={settingNavigations.EDIT_PASSWORD}
        component={EditPasswordScreen}
        options={{
          headerTitle: '비밀번호 변경',
          cardStyle: {
            backgroundColor: themeColors.WHITE,
          },
        }}
      />
      <Stack.Screen
        name={settingNavigations.DELETE_ACCOUNT}
        component={DeleteAccountScreen}
        options={{
          headerTitle: '회원탈퇴',
          cardStyle: {
            backgroundColor: themeColors.WHITE,
          },
        }}
      />
    </Stack.Navigator>
  );
}

export default SettingStackNavigator;