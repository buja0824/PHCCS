package PHCCS.service.chatroom.repository.mapper;

import PHCCS.service.chatroom.dto.Message;
import PHCCS.service.chatroom.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {

    void saveChatRoom(@Param("room") ChatRoom chatRoom);
    void deleteRoom(String roomId);
    void saveChatLog(@Param("roomId") String roomId, @Param("message") Message message, @Param("loggerId") Long loggerId);
    List<Message> getChatLog(@Param("memberId") Long memberId, @Param("roomId") String roomId);
}
