# 반려견 피부질환 AI 진단 어플리케이션 백엔드입니다.

<img width="7680" height="3840" alt="414488934-c697686d-738c-4818-b9fc-59b06d099866" src="https://github.com/user-attachments/assets/df850fd7-9173-421e-9992-5822c42848f7" />

## 시연 영상



## 📂 디렉토리 구조
📦back
 ┣ 📂src
 ┃ ┗ 📂main
 ┃ ┃ ┗ 📂java
 ┃ ┃ ┃ ┗ 📂PHCCS
 ┃ ┃ ┃ ┃ ┣ 📂common                  # 공통 기능 및 설정 (보안, 예외 처리, 유틸리티 등)
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config              # 스프링 및 외부 라이브러리 설정
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception           # 전역 예외 처리 및 커스텀 예외
 ┃ ┃ ┃ ┃ ┃ ┣ 📂file                # 파일 업로드/다운로드 관련 처리
 ┃ ┃ ┃ ┃ ┃ ┣ 📂filter              # 서블릿 필터 (보안, 로깅 등)
 ┃ ┃ ┃ ┃ ┃ ┣ 📂handler             # 인터셉터 및 핸들러 처리
 ┃ ┃ ┃ ┃ ┃ ┣ 📂jwt                 # JWT 인증 및 토큰 관리
 ┃ ┃ ┃ ┃ ┃ ┣ 📂response            # 공통 응답 포맷 구성
 ┃ ┃ ┃ ┃ ┃ ┣ 📂sse                 # Server-Sent Events (실시간 알림 등)
 ┃ ┃ ┃ ┃ ┃ ┗ 📂utility             # 기타 공통 유틸리티 클래스
 ┃ ┃ ┃ ┃ ┣ 📂service                 # 비즈니스 로직 및 API 구현부
 ┃ ┃ ┃ ┃ ┃ ┣ 📂admin               # 관리자 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┣ 📂chatroom            # 채팅 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┣ 📂comment             # 댓글 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┣ 📂member              # 일반 사용자 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┣ 📂pet                 # 반려동물 정보 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┣ 📂post                # 게시글 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┣ 📂skinimage           # 피부 이미지 관련 서비스
 ┃ ┃ ┃ ┃ ┃ ┗ 📂vet                 # 수의사 관련 서비스
 ┃ ┃ ┃ ┃ ┗ 📜BackApplication.java    # Spring Boot 애플리케이션 진입점
