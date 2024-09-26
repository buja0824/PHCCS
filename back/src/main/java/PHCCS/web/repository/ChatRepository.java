package PHCCS.web.repository;


public interface ChatRepository {
    void save(Long roomId, Long memberId, String chat);
}
