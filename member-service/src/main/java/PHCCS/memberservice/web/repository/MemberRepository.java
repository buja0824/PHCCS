package PHCCS.memberservice.web.repository;

import PHCCS.memberservice.domain.Member;
import PHCCS.memberservice.web.repository.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final MemberMapper mapper;

    public int save(Member member) {
        int save = mapper.save(member);
        return save;
    }
}
