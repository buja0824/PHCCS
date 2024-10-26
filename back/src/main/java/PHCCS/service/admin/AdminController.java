package PHCCS.service.admin;

import PHCCS.service.member.vet.repository.VetRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import PHCCS.service.admin.model.VetRequestModel;

@Slf4j
@RequiredArgsConstructor
@Controller // 일반 컨트롤러가 html 로 경과 넘겨주는거
//controller 부분
public class AdminController {

    final private VetRequestRepository vetRequestRepository;

    @GetMapping("/admin")
    public String admin(Model model){ // 모델 호출
        log.info("AdminController - admin 실행");
        List<VetRequestModel> vetRequestModels = vetRequestRepository.findAll(); // 디비 에서 값 꺼내기 ( 임시라 메모리로 함)
        log.info("admin - List<VetRequestModel>: {}", vetRequestModels);
        model.addAttribute("pendingRequests", vetRequestModels); // 모델에 값 넣기
        log.info("AdminController - admin 완료");
        return "vet-signup-allow"; // html 파일
    }

}