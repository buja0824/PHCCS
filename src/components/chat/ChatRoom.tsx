import React, { useEffect, useState } from 'react';
import { View, TextInput, FlatList, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { colors } from '@/constants';
import { useWebSocket } from '@/hooks/useWebSocket';
import { StackScreenProps } from '@react-navigation/stack';
import { BoardStackParamList } from '@/navigations/stack/BoardStackNavigator';
import Icon from 'react-native-vector-icons/Ionicons';
import { format } from 'date-fns';

type ChatRoomScreenProps = StackScreenProps<BoardStackParamList, 'ChatRoom'>;

export const ChatRoom = ({ route }: ChatRoomScreenProps) => {
  const { roomId, otherUserName } = route.params;
  const { messages, connect, disconnect, sendMessage } = useWebSocket();
  const [inputMessage, setInputMessage] = useState('');

  useEffect(() => {
    connect(roomId);
    return () => disconnect();
  }, [roomId]);

  const handleSend = () => {
    if (inputMessage.trim()) {
      sendMessage(inputMessage);
      setInputMessage('');
    }
  };

  return (
    <View style={styles.container}>
      <FlatList
        data={[...messages].reverse()}
        inverted={true}
        contentContainerStyle={{ flexGrow: 1, justifyContent: 'flex-end' }}
        renderItem={({ item }) => {
          console.log('Message:', {
            message: item.message,
            senderId: item.senderId,
            isMe: item.isMe,
            senderNickName: item.senderNickName
          });

          if (item.message.includes('님이 입장하였습니다.')) {
            return (
              <View style={styles.systemMessage}>
                <Text style={styles.systemMessageText}>
                  {item.message}
                </Text>
              </View>
            );
          }
          
          return (
            <View style={[
              styles.messageWrapper,
              item.isMe && styles.myMessageWrapper
            ]}>
              <View style={[
                styles.messageContainer,
                item.isMe ? styles.myMessage : styles.otherMessage
              ]}>
                <Text style={styles.messageText}>
                  {item.message}
                </Text>
              </View>
              <Text style={styles.timestamp}>
                {format(new Date(item.timestamp), 'HH:mm')}
              </Text>
            </View>
          );
        }}
        keyExtractor={(item, index) => `${item.senderId}-${item.timestamp}-${index}`}
      />
      <View style={styles.inputContainer}>
        <TextInput
          value={inputMessage}
          onChangeText={setInputMessage}
          style={styles.input}
          placeholder="메시지를 입력하세요"
          multiline
        />
        <TouchableOpacity 
          onPress={handleSend} 
          style={styles.sendButton}
          disabled={!inputMessage.trim()}
        >
          <Icon 
            name="send" 
            size={20} 
            color={colors.light.WHITE} 
          />
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F28080',
  },
  messageContainer: {
    marginVertical: 4,
    marginHorizontal: 12,
    maxWidth: '70%',
  },
  messageWrapper: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    marginBottom: 10,
    paddingHorizontal: 12,
  },
  myMessageWrapper: {
    flexDirection: 'row-reverse',
  },
  myMessage: {
    alignSelf: 'flex-end',
    backgroundColor: colors.light.WHITE,
    borderRadius: 20,
    borderBottomRightRadius: 4,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  otherMessage: {
    alignSelf: 'flex-start', 
    backgroundColor: colors.light.GRAY_100,
    borderRadius: 20,
    borderBottomLeftRadius: 4,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  systemMessage: {
    alignSelf: 'center',
    backgroundColor: 'rgba(128, 128, 128, 0.3)',
    paddingVertical: 4,
    paddingHorizontal: 12,
    borderRadius: 12,
    marginVertical: 8,
  },
  systemMessageText: {
    fontSize: 12,
    color: colors.light.WHITE,
    textAlign: 'center',
  },
  messageText: {
    fontSize: 15,
    paddingHorizontal: 16,
    paddingVertical: 10,
    color: colors.light.GRAY_900,
    lineHeight: 20,
  },
  timestamp: {
    fontSize: 11,
    color: colors.light.WHITE,
    marginHorizontal: 8,
    alignSelf: 'flex-end',
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 12,
    borderTopWidth: 1,
    borderTopColor: colors.light.GRAY_200,
    backgroundColor: colors.light.WHITE,
  },
  input: {
    flex: 1,
    backgroundColor: colors.light.GRAY_100,
    borderRadius: 24,
    paddingHorizontal: 20,
    paddingVertical: 10,
    fontSize: 15,
    marginRight: 8,
    maxHeight: 100,
  },
  sendButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.light.PINK_500,
    justifyContent: 'center',
    alignItems: 'center',
  },
  sendButtonText: {
    color: colors.light.WHITE,
    fontSize: 20,
  }
});
