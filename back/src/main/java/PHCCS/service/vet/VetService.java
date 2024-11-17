package PHCCS.service.vet;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.common.exception.DuplicateException;
import PHCCS.common.exception.ForbiddenException;
import PHCCS.common.exception.InternalServerEx;
import PHCCS.service.member.Member;
import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.vet.dto.VetDuplicateCheckDTO;
import PHCCS.service.vet.dto.VetRequestDTO;
import PHCCS.service.vet.dto.VetSignupDTO;
import PHCCS.service.vet.repository.VetInfoRepository;
import PHCCS.service.vet.repository.VetRequestRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class VetService {

    final private VetRequestRepository repository;
    final private VetInfoRepository vetInfoRepository;
    final private MemberRepository memberRepository;

    public Boolean processSaveAndRequest(VetSignupDTO vetSignupDTO){
        log.info("VetService - processSaveAndRequest 실행");
        log.info("processSaveAndRequest - VetSignupDTO: {}", vetSignupDTO);
        VetRequestDTO vetRequestDTO = new VetRequestDTO();
        Member member = new Member();

        // save 먼저 실행
        // VetSignupDTO -> Member로 나누기
        member.setEmail(vetSignupDTO.getEmail());
        member.setPwd(vetSignupDTO.getPwd());
        member.setName(vetSignupDTO.getName());
        member.setNickName(vetSignupDTO.getNickName());
        member.setPhoNo(vetSignupDTO.getPhoNo());
        member.setCreated(LocalDate.now());
        // 우선 일반회원으로 권한 설정 어드민 인증후 1로 변경
        member.setRole(0);
        log.info("processSaveAndRequest - Member: {}", member);

        // save(member) 실행
        Boolean isSaveSuccess = (memberRepository.save(member) > 0);

        // VetSignupDTO -> VetRequestDTO 로 나누기
        // 저장된 member에서 Id 가져오기
        Long MemberId = member.getId();
        vetRequestDTO.setMemberId(MemberId);
        vetRequestDTO.setLicenseNo(vetSignupDTO.getLicenseNo());
        vetRequestDTO.setEmail(vetSignupDTO.getEmail());
        vetRequestDTO.setName(vetSignupDTO.getName());
        vetRequestDTO.setHospitalName(vetSignupDTO.getHospitalName());
        vetRequestDTO.setHospitalAddr(vetSignupDTO.getHospitalAddr());
        vetRequestDTO.setRequestDate(LocalDate.now());
        log.info("processSaveAndRequest - VetRequestDTO: {}", vetRequestDTO);
        // saveRequest(VetRequestDTO) 실행
        saveRequest(vetRequestDTO);

        log.info("processSaveAndRequest 완료");
       return isSaveSuccess;
    }

    public void processVetRequestData(VetRequestDTO vetRequestDTO, Long memberId){
        log.info("VetService - processVetRequestData 실행");
        log.info("processVetRequestData - VetRequestDTO: {}", vetRequestDTO);
        vetRequestDTO.setMemberId(memberId);
        vetRequestDTO.setRequestDate(LocalDate.now());
        saveRequest(vetRequestDTO);
        log.info("VetService - processVetRequestData 완료");
    }
    public void saveRequest(VetRequestDTO vetRequestDTO) {
        log.info("VetService - saveRequest 실행");
        log.info("saveRequest - VetRequestDTO: {}", vetRequestDTO);

        // 수의사 인지 확인 - 수의사는 권한 요청 x
        if (isVetRole()) {
            throw new ForbiddenException("이미 수의사 권한을 가진 사용자는 인증 요청을 할 수 없습니다.");
        }

        // 중복 확인
        boolean isDuplicate = repository.existsByMemberIdAndEmailAndLicenseNo(
                vetRequestDTO.getMemberId(),
                vetRequestDTO.getEmail(),
                vetRequestDTO.getLicenseNo()
        ) > 0 ;

        log.info("isDuplicate result: {}", isDuplicate);

        if (isDuplicate) {
            log.error("Duplicate detected, throwing DuplicateException");
            throw new DuplicateException("이미 요청하셨습니다. 관리자가 승인을 할 때까지 기다려주세요.");
        }

        // 요청 저장
        Boolean isSaveRequestSuccess = repository.save(vetRequestDTO) > 0;
        if (!isSaveRequestSuccess) {
            throw new InternalServerEx("데이터베이스에 요청을 저장하지 못했습니다.");
        }
        log.info("VetService - saveRequest 완료");
    }

    public VetDuplicateCheckDTO isDuplicateVet(String email, String nickname, String phoNo, String licenseNo) {
        // existsByEmail = 1 이면 true, 0(다른값) 이면 false
        log.info("VetService - isDuplicateVet 실행");

        boolean emailDuplicate = (memberRepository.existsByEmail(email) == 1);
        boolean nicknameDuplicate = (memberRepository.existsByNickname(nickname) == 1);
        boolean phoNoDuplicate = (memberRepository.existsByPhoNo(phoNo) == 1);
        boolean licenseNoDuplicate = (vetInfoRepository.existsByLicenseNo(licenseNo) == 1);
        log.info("VetService - isDuplicateVet 완료");
        return new VetDuplicateCheckDTO(emailDuplicate, nicknameDuplicate, phoNoDuplicate, licenseNoDuplicate);

    }

    private boolean isVetRole() {
        // DB 조회 또는 SecurityContextHolder를 통해 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false; // 인증되지 않은 사용자는 ROLE_VET 권한이 없음
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_VET"));
    }
}
