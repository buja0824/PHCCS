package PHCCS.service.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import PHCCS.service.admin.model.VetRequestModel;

@Slf4j
@Controller // 일반 컨트롤러가 html 로 경과 넘겨주는거
//controller 부분
public class AdminController {

    @GetMapping("/admin")
    public String admin(Model model){ // 모델 호출
        log.info("admin 호출");
        List<VetRequestModel> vetRequestModels = testData(); // 디비 에서 값 꺼내기 ( 임시라 메모리로 함)
        model.addAttribute("pendingRequests", vetRequestModels); // 모델에 값 넣기
        return "vet-signup-allow"; // html 파일
    }


    public List<VetRequestModel> testData(){ // 임시 데이터
        List<VetRequestModel> list = new ArrayList<>();
        for(int i = 1; i <= 10; i ++){
            list.add(new VetRequestModel(Long.valueOf(i), "수의사"+i, "이메일@수의사"+i+".com", "병원"+i));
        }
        return list;
    }
}