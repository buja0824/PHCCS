import { Dimensions } from 'react-native';
import { createDrawerNavigator } from '@react-navigation/drawer';
import { NavigatorScreenParams, RouteProp, CommonActions } from '@react-navigation/native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import CustomDrawerContent from './CustomDrawerContent';
import CalendarHomeScreen from '@/screens/calendar/CalendarHomeScreen';
import MapStackNavigator, { MapStackParamList } from '../stack/MapStackNavigator';
import BoardStackNavigator, { BoardStackParamList } from '../stack/BoardStackNavigator';
import { aiNavigations, boardNavigations, colors, mainNavigations, petNavigations } from '@/constants';
import SettingStackNavigator, { SettingStackParamList } from '../stack/SettingStackNavigator';
import useThemeStore from '@/store/useThemeStore';
import CalendarStackNavigator from '../stack/CalendarStackNavigator';
import AiStackNavigator from '../stack/AiStackNavigator';
import PetStackNavigator, { PetStackParamList } from '../stack/PetStackNavigator';

export type MainDrawerParamList = {
  [mainNavigations.HOME]: NavigatorScreenParams<MapStackParamList>;
  [mainNavigations.AI]: undefined;
  [mainNavigations.CALENDAR]: undefined;
  [mainNavigations.BOARD]: NavigatorScreenParams<BoardStackParamList>;
  [mainNavigations.SETTING]: NavigatorScreenParams<SettingStackParamList>;
  [mainNavigations.PET]: NavigatorScreenParams<PetStackParamList>;
};

const Drawer = createDrawerNavigator<MainDrawerParamList>();

function DrawerIcons(route: RouteProp<MainDrawerParamList>, focused: boolean) {
  let iconName = '';

  switch (route.name) {
    case mainNavigations.HOME: {
      iconName = 'location-on';
      break;
    }
    case mainNavigations.PET: {
      iconName = 'pets';
      break;
    }
    case mainNavigations.BOARD: { 
      iconName = 'forum';
      break;
    }
    case mainNavigations.CALENDAR: {
      iconName = 'event-note';
      break;
    }
    case mainNavigations.AI: {
      iconName = 'auto-awesome';
      break;
    }
    
    case mainNavigations.SETTING: {
      iconName = 'settings';
      break;
    }
  }

  return (
    <MaterialIcons
      name={iconName}
      color={focused ? colors.light.BLACK : colors.light.GRAY_500}
      size={18}
    />
  );
}

if (global.__fbBatchedBridge) {
  const origMessageQueue = global.__fbBatchedBridge;
  const modules = origMessageQueue._remoteModuleTable;
  const methods = origMessageQueue._remoteMethodTable;
  global.findModuleByModuleAndMethodIds = (moduleId, methodId) => {
    console.log(`The problematic line code is in: ${modules[moduleId]}.${methods[moduleId][methodId]}`)
  }
}

global.findModuleByModuleAndMethodIds(4, 4);
global.findModuleByModuleAndMethodIds(38, 0);

function MainDrawerNavigator() {
  const { theme } = useThemeStore();
  
  return (
    <Drawer.Navigator
      drawerContent={(props) => <CustomDrawerContent {...props} />}
      screenOptions={({ route }) => ({
        headerShown: false,
        drawerType: 'front',
        drawerStyle: {
          width: Dimensions.get('screen').width * 0.6,
          backgroundColor: colors[theme].WHITE,
        },
        drawerActiveTintColor: colors[theme].PINK_400,
        drawerInactiveTintColor: colors[theme].BLACK,
        drawerActiveBackgroundColor: colors[theme].PINK_200,
        drawerInactiveBackgroundColor: colors[theme].WHITE,
        drawerItemStyle: {
          borderWidth: 1,
          borderColor: colors[theme].GRAY_200,
          borderRadius: 8,
          marginHorizontal: 8,
          marginVertical: 4,
        },
        drawerLabelStyle: {
          fontWeight: '600',
          color: colors[theme].BLACK,
        },
        drawerIcon: ({ focused }) => DrawerIcons(route, focused),
      })}
    >
      <Drawer.Screen
        name={mainNavigations.HOME}
        component={MapStackNavigator}
        options={{
          title: '주변 병원&약국',
          swipeEnabled: false,
        }}
      />
      <Drawer.Screen
        name={mainNavigations.PET}
        component={PetStackNavigator}
        options={{
          title: '반려동물 관리',
        }}
        listeners={({ navigation }) => ({
          drawerItemPress: (e) => {
            e.preventDefault();
            navigation.dispatch(
              CommonActions.reset({
                index: 0,
                routes: [
                  { 
                    name: mainNavigations.PET,
                    state: {
                      routes: [{ name: petNavigations.PET_HOME }],
                      index: 0,
                    }
                  },
                ],
              })
            );
          },
        })}
      />
      <Drawer.Screen 
        name={mainNavigations.BOARD} 
        component={BoardStackNavigator}
        options={{
          drawerLabel: '게시판',
          title: '게시판',
        }}
        listeners={({ navigation }) => ({
          drawerItemPress: (e) => {
            e.preventDefault();
            navigation.dispatch(
              CommonActions.reset({
                index: 0,
                routes: [
                  { 
                    name: mainNavigations.BOARD,
                    state: {
                      routes: [{ name: boardNavigations.BOARD_MENU }],
                      index: 0,
                    }
                  },
                ],
              })
            );
          },
        })}
      />
      <Drawer.Screen
        name={mainNavigations.AI}
        component={AiStackNavigator}
        options={{
          title: 'AI 검사',
        }}
        listeners={({ navigation }) => ({
          drawerItemPress: (e) => {
            e.preventDefault();
            navigation.dispatch(
              CommonActions.reset({
                index: 0,
                routes: [
                  { 
                    name: mainNavigations.AI,
                    state: {
                      routes: [{ name: aiNavigations.AI_HOME }],
                      index: 0,
                    }
                  },
                ],
              })
            );
          },
        })}
      />
      <Drawer.Screen
        name={mainNavigations.CALENDAR}
        component={CalendarStackNavigator}
        options={{
          title: '캘린더',
          swipeEnabled: false,
        }}
      />
      <Drawer.Screen
        name={mainNavigations.SETTING}
        component={SettingStackNavigator}
        options={{
          title: '설정',
          drawerItemStyle: {
            height: 0,
          },
        }}
      />
    </Drawer.Navigator>
  );
}

export default MainDrawerNavigator;
