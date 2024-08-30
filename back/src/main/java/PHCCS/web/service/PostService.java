package PHCCS.web.service;

import PHCCS.domain.Post;
import PHCCS.domain.UploadFile;
import PHCCS.file.FileStore;
import PHCCS.web.service.domain.FileDto;
import PHCCS.web.service.domain.PostDto;
import PHCCS.web.repository.PostRepository;
import PHCCS.web.repository.domain.PostModifyParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${file.dir}")
    private String fileDir;
    private final PostRepository repository;
    private final FileStore fileStore;

    @Transactional
    public ResponseEntity<?> save(Long memberId, PostDto dto, List<MultipartFile> imageFiles, List<MultipartFile> videoFiles) throws IOException {

        String storedDir = null;

        log.info("createPost()");
        log.info("dto: {}", dto);
        log.info("imageFiles: {}", imageFiles);
        log.info("videoFiles: {}", videoFiles);
        // 업로드한 이미지 저장
        if(imageFiles != null && !imageFiles.isEmpty()) {
            List<UploadFile> storeImgs = fileStore.storeFiles(imageFiles,
                    memberId,
                    dto.getTitle(),
                    dto.getCategory());
            storedDir = fileDir + dto.getCategory() + "/" + memberId + "/" + dto.getTitle() +"/";
            log.info("storeImgs: {}", storeImgs);
        }

        // 업로드한 동영상 저장
        if(videoFiles != null && !videoFiles.isEmpty()) {
            List<UploadFile> storeVids = fileStore.storeFiles(videoFiles,
                    memberId,
                    dto.getTitle(),
                    dto.getCategory());
            storedDir = "C:/spring/" + dto.getCategory() + "/" + memberId + "/" + dto.getTitle() +"/";
            log.info("storeVids: {}", storeVids);
        }
        log.info("storedDir = {}", storedDir);
        Post post = new Post();
        post.setMemberId(memberId);
        post.setCategory(dto.getCategory());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        post.setWriteTime(dto.getWriteTime());
        post.setFileDir(storedDir);
//        post.setImageFiles(storeImgs);
//        post.setVideoFiles(storeVids);

        switch (post.getCategory()){
            case "community_board":
                if(repository.communitySave(post) <= 0){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                }
                break;
            case "qna_board":
                if(repository.qnaSave(post) <= 0){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                }
                break;
            case "vet_board":
                if(repository.vetSave(post) <= 0){
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
            List<String> fileNames= fileStore.findFiles(fileDir);
            post.setFileList(fileNames);
            post.setFileDir("");
            return ResponseEntity.ok().body(post);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을수 없습니다.");
        }
    }

    public ResponseEntity<?> showAllPost(String category){
        log.info("|se|showAllPost()");
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

    /**
     * 게시글 수정시 사진파일이 수정될 경우
     * 기존 파일들을 모두 삭제한 후 디렉터리에 수정하려는 파일들을 새로 저장하기
     * 기존에 등록된 파일들을 삭제하기 위한 수정으로 수정 파라미터에 파일들이 없으면 디렉터리에 파일들은 삭제
     * 수정에 관해서는 디렉터리의 파일들을 삭제하고 재등록하는 과정이 존재하니 트랜잭션의 적용이 필요해보임
     */
    @Transactional
    public ResponseEntity<?> modifyPost(Long memberId, String category, Long postId, PostModifyParam param, List<MultipartFile> imgs, List<MultipartFile> vids) throws IOException {
        log.info("|se|modifyPost()");
        String storedDir = null;
        String findFileDir = repository.findPostDir(category, postId);
        log.info("|se|findFileDir = {}", findFileDir);
        if(findFileDir != null){
            // 해당 게시글을 통해서 저장된 파일존재
            // 해당 경로 찾아가서 파일들 삭제하기
            fileStore.deleteFiles(findFileDir);
        }
        if(imgs != null && !imgs.isEmpty()){
            fileStore.storeFiles(imgs, memberId, param.getTitle(), param.getCategory());
            storedDir = fileDir + param.getCategory() + "/" + memberId + "/" + param.getTitle() +"/";
        }
        if(vids != null && !vids.isEmpty()){
            fileStore.storeFiles(vids, memberId, param.getTitle(), param.getCategory());
            storedDir = fileDir + param.getCategory() + "/" + memberId + "/" + param.getTitle() +"/";
        }
        log.info("|se|storedDir = {}", storedDir);
        if(!param.getCategory().equals(category)){
            log.info("카테고리변경");
            Post beforePost = repository.showPost(category, postId);
            log.info("|se|beforePost = {}",beforePost.toString());
            Post afterPost = new Post();
            afterPost.setMemberId(memberId);
            afterPost.setTitle(param.getTitle());
            afterPost.setContent(param.getContent());
            afterPost.setAuthor(beforePost.getAuthor());
            afterPost.setViewCnt(beforePost.getViewCnt());
            afterPost.setFileDir(storedDir);
            afterPost.setWriteTime(beforePost.getWriteTime());
            afterPost.setUpdateTime(param.getModifyTime());
            switch (param.getCategory()){
                case "community_board":
                    if(repository.communitySave(afterPost) <= 0){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 수정 실패");
                    }
                    break;
                case "qna_board":
                    if(repository.qnaSave(afterPost) <= 0){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 수정 실패");
                    }
                    break;
                case "vet_board":
                    if(repository.vetSave(afterPost) <= 0){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 수정 실패");
                    }
                    break;
            }
            repository.deletePost(category, memberId, postId);
            log.info("삭제 끝");
        }else {
            log.info("카테고리동일");
            repository.modifyPost(memberId, postId, param, storedDir);
        }
        return ResponseEntity.ok().body("수정완료");
    }

    public void deletePost(String category, Long memberId, Long postId){
        log.info("|se|deletePost()");
        String findFileDir = repository.findPostDir(category, postId);
        repository.deletePost(category, memberId, postId);
        if(findFileDir != null){
            // 해당 게시글을 통해서 저장된 파일존재
            // 해당 경로 찾아가서 파일들 삭제하기
            fileStore.deleteFiles(findFileDir);
        }
    }

    public Resource sendFile(String filename, FileDto dto) throws MalformedURLException {
        String fullPath = fileStore.getFullPath(dto.getCategory(), dto.getId(), dto.getTitle(), filename);
        return new UrlResource("file:" + fullPath);
    }
}
