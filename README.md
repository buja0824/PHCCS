PetCare앱은 React-Native-Cli로 만들어졌습니다. 

<2024.11.19 기준 AI 분석 결과 화면과 채팅 화면 구현이 필요합니다.>

PetCare앱을 실행하기 위해선 먼저, 스프링부트로 만들어진 백엔드 서버와 플라스크로 만들어진 AI 서버를 먼저 실행해야 합니다.


실행에 성공하였다면 프론트 폴더로 이동하고 .env 파일에 당신의 GoogleMapsAPI를 입력하세요.

그리고 

터미널에서 

"yarn install" // 구동을 위해 필요한 패키지 설치
"npx react-native run-android" // 프로젝트 빌드

을(를) 입력하십시오. 

 -만약 애뮬레이터가 자동으로 켜지지 않는다면 수동으로 켜야 합니다
 
 -ex : C:\Users\yourName\AppData\Local\Android\Sdk\emulator\emulator.exe -avd Pixel_3a_API_34
