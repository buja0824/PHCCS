package PHCCS.jwt;

    public enum TokenStatus {
        VALID,              // 유효한 토큰
        INVALID,            // 유효하지 않은 토큰
        EXPIRED,            // 만료된 토큰
        INVALID_SIGNATURE,  // 서명이 잘못된 토큰
        MALFORMED,          // 형식이 잘못된 토큰
        UNKNOWN_ERROR       // 알 수 없는 오류
    }
