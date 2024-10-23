package PHCCS.service.admin;

import PHCCS.service.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    final private MemberRepository memberRepository;

    /**
    public Boolean approveVet(){
        // member -> vet 수의사로 승격, 0 -> 1로 변경
    }
     **/
}
