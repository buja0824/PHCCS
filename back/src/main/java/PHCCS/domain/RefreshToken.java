package PHCCS.domain;


import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RefreshToken {

    private String refreshToken;
}
