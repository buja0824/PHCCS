import EventSource from 'react-native-sse';
import axiosInstance from './axios';
import Toast from 'react-native-toast-message';
import { View, Text } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import { colors } from '@/constants';

let eventSource: EventSource | null = null;
let reconnectTimer: NodeJS.Timeout | null = null;
let isConnected = false;

type CustomEventType = 'message' | 'error' | 'open' | '새로운 댓들이 등록되었습니다.';

interface MessageEvent {
  data: string;
  type: CustomEventType;
  lastEventId?: string;
}

interface ErrorEvent {
  error: Error;
  type: 'error';
}

type EventListener<T> = (event: T) => void;

const clearReconnectTimer = () => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
};

export const connectSSE = async () => {
  try {
    if (eventSource) {
      console.log('SSE already connected, skipping reconnection');
      return;
    }

    clearReconnectTimer();

    const headers = {
      Authorization: String(axiosInstance.defaults.headers.common['Authorization'])
    };

    console.log('=== SSE Connection Attempt ===');
    console.log('Base URL:', axiosInstance.defaults.baseURL);
    console.log('Auth Header:', headers.Authorization);

    eventSource = new EventSource(`${axiosInstance.defaults.baseURL}/connect-sse`, {
      headers,
      withCredentials: true
    });

    // 연결 성공 이벤트
    (eventSource as any).addEventListener('open', () => {
      console.log('SSE Connection opened');
      isConnected = true;
      clearReconnectTimer();
    });

    // 메시지 수신 이벤트
    (eventSource as any).addEventListener('message', (event: MessageEvent) => {
      console.log('=== SSE Message Received ===');
      console.log('Message:', event.data);
    });

    // 에러 이벤트 
    (eventSource as any).addEventListener('error', (event: ErrorEvent) => {
      console.error('SSE connection error:', event.error);
      isConnected = false;
      eventSource?.close();
      eventSource = null;
      reconnectTimer = setTimeout(connectSSE, 5000);
    });

    // 댓글 알림 이벤트
    (eventSource as any).addEventListener('새로운 댓들이 등록되었습니다.', (event: MessageEvent) => {
      console.log('=== Comment Notification ===');
      console.log('Data:', event.data);
      
      Toast.show({
        type: 'info',
        text1: '새로운 댓글을 확인해보세요',
        text2: event.data,
        position: 'top',
        visibilityTime: 3000,
        autoHide: true,
        topOffset: 50,
        props: {
          style: {
            borderLeftColor: colors.light.PINK_500,
            borderLeftWidth: 4,
          }
        }
      });
    });

    // 채팅 초대 알림 이벤트
    (eventSource as any).addEventListener('새로운 채팅방에 초대 되었습니다.', (event: MessageEvent) => {
      console.log('=== Chat Invitation Notification ===');
      console.log('Data:', event.data);
      
      Toast.show({
        type: 'info',
        text1: '새로운 채팅 초대',
        text2: event.data,
        position: 'top',
        visibilityTime: 3000,
        autoHide: true,
        topOffset: 50,
        props: {
          style: {
            borderLeftColor: colors.light.PINK_500,
            borderLeftWidth: 4,
          }
        }
      });
    });

  } catch (error: unknown) {
    console.error('SSE Connection Error:', error);
    isConnected = false;
    reconnectTimer = setTimeout(connectSSE, 5000);
  }
};

export const disconnectSSE = () => {
  clearReconnectTimer();
  if (eventSource) {
    eventSource.close();
    eventSource = null;
    isConnected = false;
  }
};

export const checkSSEConnection = () => {
  console.log('Checking SSE connection status...', { isConnected });
  if (!isConnected || !eventSource) {
    console.log('SSE connection is not active, attempting to reconnect...');
    connectSSE();
  }
  return isConnected;
}; 