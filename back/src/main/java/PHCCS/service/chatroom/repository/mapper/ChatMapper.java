package PHCCS.service.chatroom.repository.mapper;

import PHCCS.service.chatroom.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ChatMapper {

    void saveChatRoom(@Param("room") ChatRoom chatRoom);
    void deleteRoom(String roomId);
}
