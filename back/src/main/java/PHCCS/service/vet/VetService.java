package PHCCS.service.vet;

import PHCCS.service.member.Member;
import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.vet.dto.VetDuplicateCheckDTO;
import PHCCS.service.vet.dto.VetRequestDTO;
import PHCCS.service.vet.dto.VetSignupDTO;
import PHCCS.service.vet.repository.VetInfoRepository;
import PHCCS.service.vet.repository.VetRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public boolean saveRequest(VetRequestDTO vetRequestDTO){

        log.info("VetService - saveRequest 실행");
        log.info("saveRequest - VetRequestDTO: {}", vetRequestDTO);
        Boolean isSaveRequestSuccess = repository.save(vetRequestDTO) > 0;

        log.info("VetService - saveRequest 완료");
       return isSaveRequestSuccess;
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
}
