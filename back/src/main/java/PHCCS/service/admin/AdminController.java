package PHCCS.service.admin;

import PHCCS.service.vet.repository.VetRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import PHCCS.service.admin.model.VetRequestModel;

@Slf4j
@RequiredArgsConstructor
@Controller // 일반 컨트롤러가 html 로 경과 넘겨주는거
//controller 부분
public class AdminController {

    final private VetRequestRepository vetRequestRepository;
    final private AdminService service;

    @GetMapping("/admin")
    public String admin(Model model){ // 모델 호출
        log.info("AdminController - admin 실행");
        List<VetRequestModel> vetRequestModels = vetRequestRepository.findAll(); // 디비 에서 값 꺼내기 ( 임시라 메모리로 함)
        log.info("admin - List<VetRequestModel>: {}", vetRequestModels);
        model.addAttribute("pendingRequests", vetRequestModels); // 모델에 값 넣기
        log.info("AdminController - admin 완료");
        return "vet-signup-allow"; // html 파일
    }

    @PostMapping("/admin/approveVet")
    @ResponseBody
    public ResponseEntity<?> approveVet(@RequestParam("id")Long id) {
        log.info("AdminController - admin/approveVet 실행");
        Boolean isSaveSuccess = service.saveVetInfo(id);
        Boolean isSuccess = service.promoteToVet(id);
        log.info("AdminController - admin/approveVet 완료");
        if (!isSaveSuccess) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("수의사 정보 저장에 실패했습니다.");
        } else if (!isSuccess) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("수의사 승인에 실패했습니다.");
        } else {
            return ResponseEntity.ok("수의사 승인 완료");
        }
    }
    // 거절로직 알람과 같이 추가할 필요가 있음
    @PostMapping("/admin/rejectVet")
    @ResponseBody
    public ResponseEntity<?> rejectVet(@RequestParam("id")Long id) {
        // 거절로직추가필요(거절 사유 알림)
        if(service.rejectVet(id)){
            return ResponseEntity.ok("수의사 승인 거절");
        }
        else {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("수의사 승인 거절 과정에서 오류가 발생했습니다.");}
    }
}