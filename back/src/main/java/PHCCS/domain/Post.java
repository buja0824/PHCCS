package PHCCS.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post { // 회원과 게시판은 1:N 관계일듯

    private Long id; // 게시판의 pk

    private String category; // 게시판 종류
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String author; // 게시글 작성자 : 회원의 닉네임을 받아와야 함
    private LocalDateTime writeTime = LocalDateTime.now(); // 게시글 작성날짜와 시간
    // 게시글 수정 시간은 따로 dto 받아서 거기서 정의 해야 할 듯 ? ? ?

    // 게시글 조회수, 종아요 클래스 따로 만들어서 관리하는게 좋을거 같은데
    // 그렇게 하면 db에 테이블을 따로 만들어서 조회스랑 좋아요수를 따로 약한 계체로 만들기 ?
//    private int viewCount; // 게시글 조회수
//    private int likeCount; // 게시글 좋아요 수

}
