package PHCCS.web.repository.mapper;

import PHCCS.domain.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ChatMapper {

    void saveChatRoom(@Param("room") ChatRoom chatRoom);
    void deleteRoom(String roomId);
}
