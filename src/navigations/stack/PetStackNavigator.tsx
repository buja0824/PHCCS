import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {petNavigations} from '@/constants';
import PetHomeScreen from '@/screens/pet/PetHomeScreen';
import PetAddScreen from '@/screens/pet/PetAddScreen';
import PetEditScreen from '@/screens/pet/PetEditScreen';
import PetHealthScreen from '@/screens/pet/PetHealthScreen';
import HealthCheckupScreen from '@/screens/pet/HealthCheckupScreen';
import VaccinationScreen from '@/screens/pet/VaccinationScreen';
import MedicalHistoryScreen from '@/screens/pet/MedicalHistoryScreen';
import HealthCheckupListScreen from '@/screens/pet/HealthCheckupListScreen';
import useThemeStore from '@/store/useThemeStore';
import {colors} from '@/constants';
import Ionicons from 'react-native-vector-icons/Ionicons';
import {TouchableOpacity} from 'react-native';
import { MedicalHistory, Vaccination } from '@/types/petHealth';
import VaccinationListScreen from '@/screens/pet/VaccinationListScreen';
import MedicalHistoryListScreen from '@/screens/pet/MedicalHistoryListScreen';

export type PetStackParamList = {
  [petNavigations.PET_HOME]: undefined;
  [petNavigations.PET_ADD]: undefined;
  [petNavigations.PET_EDIT]: {
    petName: string;
  };
  [petNavigations.PET_HEALTH]: {
    petName: string;
  };
  [petNavigations.HEALTH_CHECKUP]: {
    petName: string;
    vaccination?: Vaccination;
    mode?: 'add' | 'edit';
  };
  [petNavigations.HEALTH_CHECKUP_LIST]: {
    petName: string;
  };
  [petNavigations.VACCINATION]: {
    petName: string;
    vaccination?: Vaccination;
    mode?: 'add' | 'edit';
  };
  [petNavigations.VACCINATION_LIST]: {petName: string};
  [petNavigations.MEDICAL_HISTORY]: {
    petName: string;
    history?: MedicalHistory;
    mode?: 'add' | 'edit';
  };
  [petNavigations.MEDICAL_HISTORY_LIST]: {petName: string};
};

const Stack = createStackNavigator<PetStackParamList>();

function PetStackNavigator() {
  const {theme} = useThemeStore();
  
  return (
    <Stack.Navigator
      screenOptions={({navigation}) => ({
        headerLeft: () => (
          <TouchableOpacity 
            onPress={() => navigation.openDrawer()} 
            style={{marginLeft: 10}}
          >
            <Ionicons name="menu" size={25} color={colors[theme].GRAY_700} />
          </TouchableOpacity>
        ),
        headerStyle: {
          backgroundColor: colors[theme].WHITE,
        },
        headerTintColor: colors[theme].BLACK,
        headerTitleStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
        headerTitleAlign: 'center',
      })}>
      <Stack.Screen
        name={petNavigations.PET_HOME}
        component={PetHomeScreen}
        options={{
          title: '반려동물 관리',
        }}
      />
      <Stack.Screen
        name={petNavigations.PET_ADD}
        component={PetAddScreen}
        options={{
          title: '반려동물 정보 등록',
        }}
      />
      <Stack.Screen
        name={petNavigations.PET_EDIT}
        component={PetEditScreen}
        options={{
          title: '반려동물 정보 수정',
        }}
      />
      <Stack.Screen
        name={petNavigations.PET_HEALTH}
        component={PetHealthScreen}
        options={{
          title: '반려동물 건강관리',
        }}
      />
      <Stack.Screen
        name={petNavigations.HEALTH_CHECKUP}
        component={HealthCheckupScreen}
        options={{
          title: '건강검진',
        }}
      />
      <Stack.Screen
        name={petNavigations.VACCINATION}
        component={VaccinationScreen}
        options={{
          title: '예방접종',
        }}
      />
      <Stack.Screen
        name={petNavigations.MEDICAL_HISTORY}
        component={MedicalHistoryScreen}
        options={{
          title: '질병 관리',
        }}
      />
      <Stack.Screen
        name={petNavigations.HEALTH_CHECKUP_LIST}
        component={HealthCheckupListScreen}
        options={{
          title: '건강검진 기록',
        }}
      />
      <Stack.Screen
        name={petNavigations.VACCINATION_LIST}
        component={VaccinationListScreen}
        options={{
          title: '예방접종 기록',
        }}
      />
      <Stack.Screen
        name={petNavigations.MEDICAL_HISTORY_LIST}
        component={MedicalHistoryListScreen}
        options={{
          title: '질병 기록',
        }}
      />
    </Stack.Navigator>
  );
}

export default PetStackNavigator;
