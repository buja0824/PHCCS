// C:\Users\YOUR_NAME\AppData\Local\Android\Sdk\emulator\emulator.exe -avd Pixel_3a_API_34
// C:\Users\YOUR_NAM\AppData\Local\Android\Sdk\emulator\emulator.exe -avd Pixel_5_API_33
// npx react-native run-android
// npx react-native start --reset-cache
// yarn android

// if (__DEV__) {  // malformed calls from js 디버그용, 의심가는 모듈 찾기
//   const originalConsoleError = console.error;
//   console.error = (...args) => {
//     if (args[0]?.includes('malformed calls from js')) {
//       console.log('Bridge call details:', args);
//     }
//     originalConsoleError.apply(console, args);
//   };
// }
import React from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { NavigationContainer } from '@react-navigation/native';
import { LogBox, Platform } from 'react-native';
import RootNavigator from './src/navigations/root/RootNavigator';
import queryClient from './src/api/queryClient';
import Toast from 'react-native-toast-message';
import { View, Text } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';
import { useSSE } from '@/hooks/useSSE';
import { WebSocketProvider } from '@/components/chat/WebSocketProvider';
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
  // 경고 메시지 무시 설정
  const ignoreWarnings = [
    'malformed calls from js',
    'Timing.createTimer',
    'UIManager.dispatchViewManagerCommand',
  ];

  // 콘솔 에러 필터링
  const originalConsoleError = console.error;
  console.error = (...args) => {
    if (
      typeof args[0] === 'string' && 
      ignoreWarnings.some(warning => args[0].includes(warning))
    ) {
      return;
    }
    originalConsoleError.apply(console, args);
  };

  // LogBox 경고 무시
  LogBox.ignoreLogs(ignoreWarnings);

  return (
    <>
      <QueryClientProvider client={queryClient}>
        <WebSocketProvider>
          <AppContent />
        </WebSocketProvider>
        <Toast config={toastConfig} />
      </QueryClientProvider>
    </>
  );
}

export default App;
