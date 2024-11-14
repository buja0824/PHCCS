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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    final private JavaMailSender mailSender;

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
    public Boolean promoteToVet(Long requestId) {
        log.info("AdminService - promoteToVet 실행");
        Long memberId = vetRequestRepository.findMemberIdById(requestId);
        Boolean isSuccess = memberRepository.promoteToVet(memberId) > 0;

        if (isSuccess) {
            log.info("승격 성공: 메일 발송 시작");
            String email = memberRepository.findEmailById(memberId);
            sendPromotionEmail(email); // 승격 알림 이메일 발송
            log.info("승격 메일 발송 완료");

            // 승인되었으니 요청 기록에서 삭제
            deleteVetRequestById(requestId);
        }

        return isSuccess;
    }

    private Boolean deleteVetRequestById(Long requestId){
        Boolean isDeleteSuccess = vetRequestRepository.deleteById(requestId) > 0;
        return isDeleteSuccess;
    }

    // 요청 거절 메소드
    public Boolean rejectVet(Long requestId) {
        log.info("AdminService - rejectVetRequest 실행");
        Long memberId = vetRequestRepository.findMemberIdById(requestId);
        String email = memberRepository.findEmailById(memberId);

        // 요청 거절 메일 발송
        sendRejectionEmail(email);

        // 거절된 요청 기록 삭제
        deleteVetRequestById(requestId);
        log.info("거절 처리 완료");
        return true;
    }
    // 승격 알림 이메일 발송
    private void sendPromotionEmail(String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("승격 알림");
            message.setText("안녕하세요, 회원님. 귀하의 계정이 수의사로 승격되었습니다. 축하드립니다!");
            mailSender.send(message);
            log.info("승격 이메일 발송 성공: {}", email);
        } catch (Exception e) {
            log.error("승격 이메일 발송 실패: {}", e.getMessage(), e);
        }
    }

    // 거절 알림 이메일 발송
    private void sendRejectionEmail(String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("요청 거절 알림");
            message.setText("안녕하세요, 회원님. 귀하의 수의사 승격 요청이 거절되었습니다. 문의 사항이 있다면 관리자에게 문의해 주세요.");
            mailSender.send(message);
            log.info("거절 이메일 발송 성공: {}", email);
        } catch (Exception e) {
            log.error("거절 이메일 발송 실패: {}", e.getMessage(), e);
        }
    }
}
