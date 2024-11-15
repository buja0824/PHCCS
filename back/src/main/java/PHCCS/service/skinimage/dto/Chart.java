package PHCCS.service.skinimage.dto;

import lombok.Data;

@Data
public class Chart {
    private Boolean breed;      // 강아지면 0, 고양이면 1
    private Boolean symptom;    // 무증상 0, 유증상 1
}
