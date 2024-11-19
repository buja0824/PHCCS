// C:\Users\junse\AppData\Local\Android\Sdk\emulator\emulator.exe -avd Pixel_3a_API_34
// npx react-native run-android
// npx react-native start --reset-cache
// yarn android

import React from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { NavigationContainer } from '@react-navigation/native';
import RootNavigator from './src/navigations/root/RootNavigator';
import queryClient from './src/api/queryClient';
import Toast from 'react-native-toast-message';
import { View, Text } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { useSSE } from '@/hooks/useSSE';
import Config from 'react-native-config';

function AppContent() {
  useSSE();
  return (
    <NavigationContainer>
      <RootNavigator />
    </NavigationContainer>
  );
}

const toastConfig = {
  info: (props: any) => (
    <View
      pointerEvents="none"
      style={[{
        width: '90%',
        backgroundColor: '#fff',
        borderRadius: 12,
        padding: 16,
        marginHorizontal: 20,
        flexDirection: 'row',
        alignItems: 'center',
        shadowColor: '#000',
        shadowOffset: {
          width: 0,
          height: 2,
        },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5,
      }, props.props?.style]}>
      <View style={{ marginRight: 12 }}>
        <Icon name="notifications" size={24} color={colors.light.PINK_500} />
      </View>
      <View style={{ flex: 1 }}>
        <Text style={{
          fontSize: 15,
          fontWeight: '600',
          color: colors.light.GRAY_900,
          marginBottom: 4,
        }}>
          {props.text1}
        </Text>
        <Text style={{
          fontSize: 15,
          fontWeight: '500',
          color: colors.light.GRAY_700,
          lineHeight: 20,
        }}>
          {props.text2}
        </Text>
      </View>
    </View>
  ),
};

function App() {
  return (
    <>
      <QueryClientProvider client={queryClient}>
        <AppContent />
      </QueryClientProvider>
      <Toast config={toastConfig} />
    </>
  );
}

export default App;
