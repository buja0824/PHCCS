package PHCCS.service.admin;

import PHCCS.service.member.MemberService;
import PHCCS.service.member.dto.MemberDTO;
import PHCCS.service.member.exception.LoginFailedException;
import PHCCS.service.vet.repository.VetRequestRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import PHCCS.service.admin.model.VetRequestModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller // 일반 컨트롤러가 html 로 경과 넘겨주는거
//controller 부분
public class AdminController {

    final private VetRequestRepository vetRequestRepository;
    final private AdminService service;
    final private MemberService memberService;

    @GetMapping("/admin")
    public String showAdminLoginForm() {
        return "admin-login";
    }

    @PostMapping("/admin/signin")
    public String AdminSignin(@ModelAttribute MemberDTO memberDTO, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            // 로그인 로직 호출
            Map<String, String> tokens = memberService.login(memberDTO);

            // Access Token 설정
            Cookie accessTokenCookie = new Cookie("accessToken", tokens.get("accessToken"));
            accessTokenCookie.setHttpOnly(false);
            accessTokenCookie.setSecure(false); // HTTPS 환경에서 true
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(10 * 60); // 10분 유효
            response.addCookie(accessTokenCookie);

            // Refresh Token 설정
            Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // HTTPS 환경에서 true
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 유효
            response.addCookie(refreshTokenCookie);

            // 로그인 성공 시 리다이렉트
            return "redirect:/admin/requests";
        } catch (LoginFailedException e) {
            // 로그인 실패 예외 처리
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin"; // 로그인 페이지로 리다이렉트
        } catch (Exception e) {
            // 기타 예외 처리
            redirectAttributes.addFlashAttribute("error", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin";
        }
    }


    @GetMapping("/admin/requests")
    public String listVetRequests(Model model){ // 모델 호출
        log.info("AdminController - admin/signin 실행");
        List<VetRequestModel> vetRequestModels = vetRequestRepository.findAll(); // 디비 에서 값 꺼내기 ( 임시라 메모리로 함)
        log.info("admin - List<VetRequestModel>: {}", vetRequestModels);
        model.addAttribute("pendingRequests", vetRequestModels); // 모델에 값 넣기
        log.info("AdminController - admin/signin 완료");
        return "vet-signup-allow"; // html 파일
    }

    @PostMapping("/admin/approveVet")
    public String approveVet(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("AdminController - admin/approveVet 실행");

        Boolean isSaveSuccess = service.saveVetInfo(id);
        Boolean isSuccess = service.promoteToVet(id);

        log.info("AdminController - admin/approveVet 완료");

        if (!isSaveSuccess) {
            redirectAttributes.addFlashAttribute("error", "수의사 정보 저장에 실패했습니다.");
        } else if (!isSuccess) {
            redirectAttributes.addFlashAttribute("error", "수의사 승인에 실패했습니다.");
        } else {
            redirectAttributes.addFlashAttribute("success", "수의사 승인 완료");
        }

        // 요청 처리 후 "/admin/requests"로 리다이렉트
        return "redirect:/admin/requests";
    }

    @PostMapping("/admin/rejectVet")
    public String rejectVet(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        log.info("AdminController - admin/rejectVet 실행");

        try {
            // 거절 로직 수행
            Boolean isRejected = service.rejectVet(id);

            if (isRejected) {
                redirectAttributes.addFlashAttribute("success", "수의사 승인이 거절되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "수의사 승인 거절 과정에서 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            log.error("Error while rejecting vet: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "수의사 승인 거절 중 예외가 발생했습니다.");
        }

        // 요청 목록 페이지로 리다이렉트
        return "redirect:/admin/requests";
    }

}

