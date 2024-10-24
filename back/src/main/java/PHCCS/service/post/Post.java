package PHCCS.service.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post { // 회원과 게시판은 1:N 관계일듯

    private Long id; // 게시판의 pk

    private Long memberId; // 멤버의 기본키 : FK이다
    private String category; // 게시판 종류
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String author; // 게시글 작성자
    // 글 작성자 : 회원의 닉네임을 받아와야 함
    private LocalDateTime writeTime; // 게시글 작성날짜와 시간
    private String updateTime;// 게시글 수정날짜와 시간
    private Long viewCnt;
    private Long likeCnt;
    //file 관련 멤버변수
//    private List<UploadFile> imageFiles;
//    private List<UploadFile> videoFiles;
    private String fileDir;
    private List<String> fileList;

    // 게시글 조회수, 종아요 클래스 따로 만들어서 관리하는게 좋을거 같은데
    // 그렇게 하면 db에 테이블을 따로 만들어서 조회스랑 좋아요수를 따로 약한 계체로 만들기 ?

}
