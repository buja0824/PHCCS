package PHCCS.service.skinimage;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SkinImage {

    private Long id;
    private Long memberId;
    private LocalDateTime createAt;
    private String dir;
    private String result;
    private String breed;
    private String symptom;
}
