package practice.pra.domain.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import practice.pra.domain.Member;
import practice.pra.domain.repository.MemberSearchCon;
import practice.pra.domain.repository.MemberUpdateDto;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    void save(Member member);

    void update(@Param("id") Long id, @Param("updateParam") MemberUpdateDto updateParam);

    Optional<Member> findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    String findLoginIdByNameAndEmail(@Param("name") String name, @Param("email")String email);

    Optional<Member> findPwdByIdAndEmail(@Param("id") String id, @Param("email")String email);

    List<Member> findAll(MemberSearchCon memberSearch);
}
