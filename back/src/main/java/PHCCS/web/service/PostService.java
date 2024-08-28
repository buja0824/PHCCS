package PHCCS.web.service;

import PHCCS.domain.Post;
import PHCCS.domain.UploadFile;
import PHCCS.file.FileStore;
import PHCCS.web.controller.FileDto;
import PHCCS.web.controller.PostDto;
import PHCCS.web.repository.PostRepository;
import PHCCS.web.repository.domain.PostModifyParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final FileStore fileStore;

    public ResponseEntity<?> save(Long memberId, PostDto dto, List<MultipartFile> imageFiles, List<MultipartFile> videoFiles) throws IOException {

        String fileDir = null;

        log.info("createPost()");
        log.info("dto: {}", dto);
        log.info("imageFiles: {}", imageFiles);
        log.info("videoFiles: {}", videoFiles);
        // 업로드한 이미지 저장
        if(imageFiles != null && !imageFiles.isEmpty()) {
            List<UploadFile> storeImgs = fileStore.storeFiles(imageFiles,
                    /*loginMember.getId()*/2L,
                    dto.getTitle(),
                    dto.getCategory());
            fileDir = "C:/spring/" + dto.getCategory() + "/" + /*loginMember.getId()*/2L + "/" + dto.getTitle() +"/";
            log.info("storeImgs: {}", storeImgs);
        }

        // 업로드한 동영상 저장
        if(videoFiles != null && !videoFiles.isEmpty()) {
            List<UploadFile> storeVids = fileStore.storeFiles(videoFiles,
                    /*loginMember.getId()*/2L,
                    dto.getTitle(),
                    dto.getCategory());
            fileDir = "C:/spring/" + dto.getCategory() + "/" + /*loginMember.getId()*/2L + "/" + dto.getTitle() +"/";
            log.info("storeVids: {}", storeVids);
        }
        log.info("fileDir = {}", fileDir);
        Post post = new Post();
        post.setCategory(dto.getCategory());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        post.setWriteTime(dto.getWriteTime());
        post.setFileDir(fileDir);
//        post.setImageFiles(storeImgs);
//        post.setVideoFiles(storeVids);

        switch (post.getCategory()){
            case "community_board":
                if(repository.communitySave(memberId, post) <= 0){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                }
                break;
            case "qna_board":
                if(repository.qnaSave(memberId, post) <= 0){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                }
                break;
            case "vet_board":
                if(repository.vetSave(memberId, post) <= 0){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                }
                break;
        }
        return ResponseEntity.ok("게시글을 등록 하였습니다.");
    }

    public ResponseEntity<?> showPost(String category, Long id){

        if(category != null && !category.isEmpty() && id != 0L){
            Post post = repository.showPost(category, id);
            String fileDir = post.getFileDir();
            List<String> files = fileStore.findFiles(fileDir);
            post.setFileList(files);
            post.setFileDir("");
            return ResponseEntity.ok().body(post);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을수 없습니다.");
        }
    }

    public ResponseEntity<?> showAllPost(String category){
        log.info("| |showAllPost()");
        log.info("category = {}", category);
        if(category == null || category.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 접근입니다.");

        try {
            List<Post> posts = repository.showAllPost(category);
            log.info("posts = {}", posts.toString());
            if(posts != null && !posts.isEmpty()){
                return ResponseEntity.ok().body(posts);
            }
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글을 불러오지 못하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글이 존재하지 않습니다..");
    }


    public void modifyPost(Long id, PostModifyParam param){

    }

    public Resource sendFile(String filename, FileDto dto) throws MalformedURLException {
        String fullPath = fileStore.getFullPath(dto.getCategory(), dto.getId(), dto.getTitle(), filename);
        return new UrlResource("file:" + fullPath);
    }
}
