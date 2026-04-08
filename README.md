# 반려견 피부질환 AI 진단 어플리케이션 백엔드입니다.

<img width="7680" height="3840" alt="414488934-c697686d-738c-4818-b9fc-59b06d099866" src="https://github.com/user-attachments/assets/df850fd7-9173-421e-9992-5822c42848f7" />

## 📺 시연 영상

### 회원가입
https://github.com/user-attachments/assets/d609cd8c-10b4-4ce3-b757-7cdba0313aad

### AI 진단
https://github.com/user-attachments/assets/24c2adbf-ff4d-4719-be44-2d15129a7b27

### 게시글 작성 및 댓글 알림
https://github.com/user-attachments/assets/562b9eed-0403-4f08-a27b-1519c90d4575

### 채팅
https://github.com/user-attachments/assets/ef7b1d46-4898-425e-b360-50b0a0334f1d

### 지도
https://github.com/user-attachments/assets/df496833-d0aa-4fc3-a434-ba22578082c0

###  건강관리
https://github.com/user-attachments/assets/6da24b5c-aca1-42f2-bbb5-6504b5c10d8f

### 설정
https://github.com/user-attachments/assets/e068b333-1a96-48a8-af76-f0081ff83123


## 📂 디렉토리 구조
```text
📦 back
 ┗ 📂 src
   ┗ 📂 main
     ┗ 📂 java
       ┗ 📂 PHCCS
         ┣ 📂 common          # 공통 기능 및 설정 (보안, 예외 처리, 유틸리티 등)
         ┃ ┣ 📜 config        # 스프링 및 외부 라이브러리 설정
         ┃ ┣ 📜 exception     # 전역 예외 처리 및 커스텀 예외
         ┃ ┣ 📜 file          # 파일 업로드/다운로드 관련 처리
         ┃ ┣ 📜 filter        # 서블릿 필터 (보안, 로깅 등)
         ┃ ┣ 📜 handler       # 인터셉터 및 핸들러 처리
         ┃ ┣ 📜 jwt           # JWT 인증 및 토큰 관리
         ┃ ┣ 📜 response      # 공통 응답 포맷 구성
         ┃ ┣ 📜 sse           # Server-Sent Events (실시간 알림 등)
         ┃ ┗ 📜 utility       # 기타 공통 유틸리티 클래스
         ┣ 📂 service         # 비즈니스 로직 및 API 구현부
         ┃ ┣ 📜 admin         # 관리자 관련 서비스
         ┃ ┣ 📜 chatroom      # 채팅 관련 서비스
         ┃ ┣ 📜 comment       # 댓글 관련 서비스
         ┃ ┣ 📜 member        # 일반 사용자 관련 서비스
         ┃ ┣ 📜 pet           # 반려동물 정보 관련 서비스
         ┃ ┣ 📜 post          # 게시글 관련 서비스
         ┃ ┣ 📜 skinimage     # 피부 이미지 관련 서비스
         ┃ ┗ 📜 vet           # 수의사 관련 서비스
         ┗ 📜 BackApplication.java  # 애플리케이션 진입점
