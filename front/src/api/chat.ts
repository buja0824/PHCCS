import axiosInstance from './axios';

export interface ChatRoom {
  roomId: string;
  roomName: string;
  createMemberId: number;
  invitedMemberId: number;
}

export interface CreateChatRoomRequest {
  createMemberId: number;
  participatingMemberId: number;
  roomName: string;
}

export const createChatRoom = async (participatingMemberId: number, roomName: string): Promise<ChatRoom> => {
  const { data } = await axiosInstance.post('/chat', {
    participatingMemberId,
    roomName
  });
  return data;
};

export const getChatRooms = async (): Promise<ChatRoom[]> => {
  const { data } = await axiosInstance.get('/chat');
  return data;
};
