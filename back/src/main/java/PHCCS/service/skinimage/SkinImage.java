package PHCCS.service.skinimage;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SkinImage {

    private Long id;
    private Long memberId;
    private LocalDate createAt;
    private String dir;
    private String result;
}
