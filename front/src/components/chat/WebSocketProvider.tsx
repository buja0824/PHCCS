import React, { createContext, useContext, useEffect, useState } from 'react';
import axiosInstance from '@/api/axios';
import { Platform } from 'react-native';
import useAuth from '@/hooks/queries/useAuth';

interface Message {
  message: string;
  senderId: number;
  senderNickName: string;
  timestamp: string;
  isMe: boolean;
}

interface WebSocketContextType {
  messages: Message[];
  sendMessage: (message: string) => void;
  connect: (roomId: string) => void;
  disconnect: () => void;
}

export const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
  const [ws, setWs] = useState<WebSocket | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const { getProfileQuery } = useAuth();
  const { data: userProfile } = getProfileQuery;

  const connect = (roomId: string) => {
    const token = axiosInstance.defaults.headers.common['Authorization'];
    
    if (!token) {
      console.error('Authorization token is missing');
      return;
    }

    const cleanToken = String(token).replace('Bearer ', '');
    
    const wsUrl = Platform.OS === 'android' 
      ? `ws://10.0.2.2:3030/ws/chat?roomId=${roomId}&token=${cleanToken}`
      : `ws://localhost:3030/ws/chat?roomId=${roomId}&token=${cleanToken}`;
      
    console.log('Connecting to WebSocket:', wsUrl);
    
    const socket = new WebSocket(wsUrl);
    
    socket.onopen = () => {
      console.log('WebSocket Connected');
    };

    socket.onmessage = (e) => {
      try {
        if (typeof e.data === 'string' && !e.data.includes('채팅방 입장 성공')) {
          const message = JSON.parse(e.data);
          console.log('Received message:', message);
          
          const messageWithTimestamp = {
            ...message,
            timestamp: message.timestamp || new Date().toISOString(),
          };
          
          setMessages(prev => [...prev, messageWithTimestamp]);
        }
      } catch (error) {
        console.error('WebSocket message parsing error:', error, e.data);
      }
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    socket.onclose = (event) => {
      console.log('WebSocket closed:', event.code, event.reason);
    };

    setWs(socket);
  };

  const disconnect = () => {
    if (ws) {
      ws.close();
      setWs(null);
      setMessages([]);
    }
  };

  const sendMessage = (message: string) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      const messageData = {
        message,
        type: 'TALK'
      };
      ws.send(JSON.stringify(messageData));
    }
  };

  useEffect(() => {
    return () => {
      disconnect();
    };
  }, []);

  return (
    <WebSocketContext.Provider value={{ messages, sendMessage, connect, disconnect }}>
      {children}
    </WebSocketContext.Provider>
  );
};
