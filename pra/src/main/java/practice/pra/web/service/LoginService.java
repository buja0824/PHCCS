package practice.pra.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.pra.domain.Member;
import practice.pra.domain.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository repository;

    public Member findLoginMember(String email){

        Member findMember = repository.findByEmail(email).get();

        return findMember;
    }
}
