package PHCCS.service.admin;

import PHCCS.common.jwt.JwtUtil;
import PHCCS.service.member.Member;
import PHCCS.service.member.MemberService;
import PHCCS.service.member.dto.MemberDTO;
import PHCCS.service.member.exception.LoginFailedException;
import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.member.token.TokenService;
import PHCCS.service.vet.dto.VetInfoDTO;
import PHCCS.service.vet.dto.VetRequestDTO;
import PHCCS.service.vet.repository.VetInfoRepository;
import PHCCS.service.vet.repository.VetRequestRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    final private MemberRepository memberRepository;
    final private VetRequestRepository vetRequestRepository;
    final private VetInfoRepository vetInfoRepository;
    final private MemberService memberService;
    final private JwtUtil jwtUtil;
    final private TokenService tokenService;

    public Boolean saveVetInfo(Long requestId){

        log.info("AdminService - saveVetInfo 실행");
        VetRequestDTO requestDTO = vetRequestRepository.findById(requestId);
        VetInfoDTO infoDTO = new VetInfoDTO();
        infoDTO.setMemberId(requestDTO.getMemberId());
        infoDTO.setLicenseNo(requestDTO.getLicenseNo());
        infoDTO.setHospitalName(requestDTO.getHospitalName());
        infoDTO.setHospitalAddr(requestDTO.getHospitalAddr());

        Boolean isSaveSuccess = vetInfoRepository.save(infoDTO) > 0;

        log.info("AdminService - saveVetInfo 완료");

        return isSaveSuccess;
    }
    // 일반 멤버에서 Vet으로 승격시키는 메소드
    public Boolean promoteToVet(Long requestId){

        log.info("AdminService - promoteToVet 실행");
        Long MemberId = vetRequestRepository.findMemberIdById(requestId);
        Boolean isSuccess = memberRepository.promoteToVet(MemberId) > 0;
        log.info(isSuccess.toString());
        log.info("AdminService - promoteToVet 완료");

        // 승인되었으니 요청 기록에서 삭제
        deleteVetRequestById(requestId);

        return  isSuccess;
    }

    private Boolean deleteVetRequestById(Long requestId){
        Boolean isDeleteSuccess = vetRequestRepository.deleteById(requestId) > 0;
        return isDeleteSuccess;
    }

    public Boolean rejectVet(Long requestId){
        return deleteVetRequestById(requestId);
    }

}
