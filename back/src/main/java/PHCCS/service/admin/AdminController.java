package PHCCS.service.admin;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller // 일반 컨트롤러가 html 로 경과 넘겨주는거
public class AdminController {

    @GetMapping("/admin")
    public String admin(Model model){ // 모델 호출
        log.info("admin 호출");
        List<Request> requests = testData(); // 디비 에서 값 꺼내기 ( 임시라 메모리로 함)
        model.addAttribute("pendingRequests", requests); // 모델에 값 넣기
        return "vet-signup-allow"; // html 파일
    }


    public List<Request> testData(){ // 임시 데이터
        List<Request> list = new ArrayList<>();
        for(int i = 1; i <= 10; i ++){
            list.add(new Request(Long.valueOf(i), "수의사"+i, "이메일@수의사"+i+".com", "병원"+i));
        }
        return list;
    }
}

@Data
class Request{
    private Long id;
    private String name;
    private String email;
    private String hospitalName;
    private String requestDate;

    public Request(Long id, String name, String email, String hospitalName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hospitalName = hospitalName;
        this.requestDate = LocalDate.now()+"";
    }
}
