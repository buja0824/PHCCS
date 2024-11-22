import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {aiNavigations, colors} from '@/constants';
import Ionicons from 'react-native-vector-icons/Ionicons';
import {TouchableOpacity, Image} from 'react-native';
import useThemeStore from '@/store/useThemeStore';
import AiHomeScreen from '@/screens/ai/AiScanHomeScreen';
import AiPetTypeScreen from '@/screens/ai/AiPetTypeScreen';
import AiSymptomTypeScreen from '@/screens/ai/AiSymptomTypeScreen';
import AiCameraScreen from '@/screens/ai/AiCameraScreen';
import AiResultScreen from '@/screens/ai/AiResultScreen';

export type AiStackParamList = {
  [aiNavigations.AI_HOME]: undefined;
  [aiNavigations.AI_PET_TYPE]: undefined;
  [aiNavigations.AI_SYMPTOM_TYPE]: {
    petType: 'dog' | 'cat';
  };
  [aiNavigations.AI_CAMERA]: {
    petType: 'dog' | 'cat';
    hasSymptom: boolean;
  };
  [aiNavigations.AI_RESULT]: {
    result: {
      imgResult: string;
      fileName: string;
    };
    petType: 'dog' | 'cat';
    hasSymptom: boolean;
  };
};

const Stack = createStackNavigator<AiStackParamList>();

function AiStackNavigator() {
  const {theme} = useThemeStore();

  return (
    <Stack.Navigator
      screenOptions={({navigation}) => ({
        headerLeft: ({canGoBack}) => 
          canGoBack ? (
            <TouchableOpacity onPress={() => navigation.goBack()}>
              <Image 
                source={require('@/assets/images/back.png')} 
                style={{width: 24, height: 24, marginLeft: 10}} 
              />
            </TouchableOpacity>
          ) : (
            <TouchableOpacity 
              onPress={() => navigation.openDrawer()} 
              style={{marginLeft: 10}}
            >
              <Ionicons name="menu" size={25} color={colors[theme].GRAY_700} />
            </TouchableOpacity>
          ),
        headerTitleAlign: 'center',
        headerTitleStyle: {
          fontSize: 16,
          fontWeight: 'bold',
        },
        headerStyle: {
          backgroundColor: colors[theme].WHITE,
          shadowColor: colors[theme].GRAY_200,
        },
      })}>
      <Stack.Screen
        name={aiNavigations.AI_HOME}
        component={AiHomeScreen}
        options={{title: 'AI 검사'}}
      />
      <Stack.Screen
        name={aiNavigations.AI_PET_TYPE}
        component={AiPetTypeScreen}
        options={{title: '반려동물 선택'}}
      />
      <Stack.Screen
        name={aiNavigations.AI_SYMPTOM_TYPE}
        component={AiSymptomTypeScreen}
        options={{title: '증상 유형'}}
      />
      <Stack.Screen
        name={aiNavigations.AI_CAMERA}
        component={AiCameraScreen}
        options={{title: '사진 촬영'}}
      />
      <Stack.Screen 
        name={aiNavigations.AI_RESULT}
        component={AiResultScreen}
        options={{
          title: 'AI 진단 결과'
        }}
      />
    </Stack.Navigator>
  );
}

export default AiStackNavigator;
