package PHCCS.service.member.vet;

import PHCCS.service.member.Member;
import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.member.vet.dto.VetRequestDTO;
import PHCCS.service.member.vet.dto.VetSignupDTO;
import PHCCS.service.member.vet.repository.VetRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class VetService {

    final private VetRequestRepository repository;
    final private MemberRepository memberRepository;

    public Boolean processSaveAndRequest(VetSignupDTO vetSignupDTO){
        VetRequestDTO vetRequestDTO = new VetRequestDTO();
        Member member = new Member();

        // VetSignupDTO -> VetRequestDTO 로 나누기
        vetRequestDTO.setLicenseNo(vetSignupDTO.getLicenseNo());
        vetRequestDTO.setEmail(vetSignupDTO.getEmail());
        vetRequestDTO.setName(vetSignupDTO.getName());
        vetRequestDTO.setHospitalName(vetSignupDTO.getHospitalName());
        vetRequestDTO.setHospitalAddr(vetRequestDTO.getHospitalAddr());
        vetRequestDTO.setRequestDate(LocalDate.now());

        // VetSignupDTO -> Member로 나누기
        member.setEmail(vetSignupDTO.getEmail());
        member.setPwd(vetSignupDTO.getPwd());
        member.setName(vetSignupDTO.getName());
        member.setNickName(vetSignupDTO.getNickName());
        member.setPhoNo(vetSignupDTO.getPhoNo());
        member.setCreated(LocalDate.now());
        // 우선 일반회원으로 권한 설정 어드민 인증후 1로 변경
        member.setRole(0);


        //  requestApproval(VetRequestDTO), save(member) 실행
        saveRequest(vetRequestDTO);

        Boolean isSaveSuccess = (memberRepository.save(member) > 0);

       return isSaveSuccess;
    }
    public boolean saveRequest(VetRequestDTO vetRequestDTO){

        vetRequestDTO.setRequestDate(LocalDate.now());

        Boolean isSaveRequestSuccess = repository.save(vetRequestDTO) > 0;

       return isSaveRequestSuccess;
    }

}
