package PHCCS.memberservice.web.repository.mapper;
import PHCCS.memberservice.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    int save(Member member);
}
