package PHCCS.service.post;

import PHCCS.common.file.UploadFile;
import PHCCS.common.file.FileStore;
import PHCCS.common.file.FileDTO;


import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.post.dto.*;
import PHCCS.service.post.repository.PostRepository;
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
    private final MemberRepository memberRepository;
    private final FileStore fileStore;

    @Transactional
    public ResponseEntity<?> save(Long memberId, PostDTO dto, List<MultipartFile> imageFiles, List<MultipartFile> videoFiles) throws IOException {

        String storedDir = null;
        List<UploadFile> storeImgs = null;
        List<UploadFile> storeVids = null;
        log.info("createPost()");
        log.info("dto: {}", dto);
        log.info("imageFiles: {}", imageFiles);
        log.info("videoFiles: {}", videoFiles);
        try {
            // 업로드한 이미지 저장
            if (imageFiles != null && !imageFiles.isEmpty()) {
                storeImgs = fileStore.storeFiles(imageFiles,
                        memberId,
                        dto.getTitle(),
                        dto.getCategory());
                storedDir = fileDir + dto.getCategory() + "/" + memberId + "/" + dto.getTitle() + "/";
                log.info("storeImgs: {}", storeImgs);
            }

            // 업로드한 동영상 저장
            if (videoFiles != null && !videoFiles.isEmpty()) {
                storeVids = fileStore.storeFiles(videoFiles,
                        memberId,
                        dto.getTitle(),
                        dto.getCategory());

                storedDir = "C:/spring/" + dto.getCategory() + "/" + memberId + "/" + dto.getTitle() + "/";
                log.info("storeVids: {}", storeVids);
            }
            log.info("storedDir = {}", storedDir);
            String nickName = memberRepository.findMemberProfileById(memberId).get().getNickName();
            Post post = new Post();
            post.setMemberId(memberId);
            post.setCategory(dto.getCategory());
            post.setTitle(dto.getTitle());
            post.setContent(dto.getContent());
            post.setAuthor(nickName);
            post.setWriteTime(dto.getWriteTime());
            post.setFileDir(storedDir);
//        post.setImageFiles(storeImgs);
//        post.setVideoFiles(storeVids);

            if (repository.save(dto.getCategory(), post) <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
            }
        }catch (Exception e){
            if(storeImgs != null || storeVids != null) fileStore.deleteFiles(storedDir);
            throw e;
        }
        return ResponseEntity.ok("게시글을 등록 하였습니다.");
    }
    @Transactional
    public ResponseEntity<?> showPost(String category, Long id){
        if(category != null && !category.isEmpty() && id != 0L){
            repository.incrementViewCount(category, id);
            Post post = repository.showPost(category, id);
            if(post == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }
            String fileDir = post.getFileDir();
            if(fileDir != null) {
                List<String> fileNames = fileStore.findFiles(fileDir);
                post.setFileList(fileNames);
                post.setFileDir("");
            }
            post.setCategory(category);
            return ResponseEntity.ok(post);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을수 없습니다.");
        }
    }

    public List<PostHeaderDTO> showAllPost(String category, String searchName, Long page, Long size){
        log.info("|se|showAllPost()");
        Long offset = (page - 1) * size; // 사이즈의 배수로 페이지 단위를 끊어서 읽어오게 하기 위함

        try {
            List<PostHeaderDTO> posts = repository.showAllPost(category, searchName, offset, size);
            log.info("posts = {}", posts.toString());
            if(posts != null && !posts.isEmpty()){
                return posts;
            }
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * 게시글 수정시 사진파일이 수정될 경우
     * 기존 파일들을 모두 삭제한 후 디렉터리에 수정하려는 파일들을 새로 저장하기
     * 기존에 등록된 파일들을 삭제하기 위한 수정으로 수정 파라미터에 파일들이 없으면 디렉터리에 파일들은 삭제
     * 수정에 관해서는 디렉터리의 파일들을 삭제하고 재등록하는 과정이 존재하니 트랜잭션의 적용이 필요해보임
     */
    @Transactional
    public ResponseEntity<?> updatePost(Long memberId, String category, Long postId, PostUpdateDTO param, List<MultipartFile> imgs, List<MultipartFile> vids) throws IOException {
        log.info("|se|updatePost()");
        String storedDir =
                fileDir + param.getCategory() + "/" + memberId + "/" + param.getTitle() +"/"; // 새로운 저장 경로
        String findFileDir =
                repository.findPostDir(category, postId); // 기존 저장 경로
        log.info("|se|findFileDir = {}", findFileDir);
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
            if(repository.save(param.getCategory(), afterPost) <=0){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
            }else {
                repository.deletePost(category, memberId, postId);
                log.info("삭제 끝");
            }
        }else {
            log.info("카테고리동일");
            repository.updatePost(memberId, postId, param, storedDir);
        }
        if(findFileDir != null){
            // 해당 게시글을 통해서 저장된 파일존재 확인
            // 해당 경로 찾아가서 파일들 삭제하기
            fileStore.deleteFiles(findFileDir);
        }
        if(imgs != null && !imgs.isEmpty()){
            fileStore.storeFiles(imgs, memberId, param.getTitle(), param.getCategory());
        }
        if(vids != null && !vids.isEmpty()){
            fileStore.storeFiles(vids, memberId, param.getTitle(), param.getCategory());
        }
        return ResponseEntity.ok().body("수정완료");
    }

    @Transactional
    public void deletePost(String category, Long memberId, Long postId){
        log.info("|se|deletePost()");
        String findFileDir = repository.findPostDir(category, postId);
        repository.deletePost(category, memberId, postId);

        if(findFileDir != null){
            try {
                // 해당 게시글을 통해서 저장된 파일존재
                // 해당 경로 찾아가서 파일들 삭제하기
                fileStore.deleteFiles(findFileDir);
            }catch (Exception e){
                throw e;
            }
        }
    }

    public List<MyPostDTO> showMyPost(Long memberId){
        return repository.showMyPost(memberId);
    }

    public List<LikedPostDTO> showLikedPosts(Long memberId){
        return repository.showLikedPosts(memberId);
    }

    @Transactional
    public boolean likePost(Long memberId, String category, Long postId){
        log.info("|se|incrementLike()");

        Boolean likeMember = repository.isLikeMember(memberId, category, postId);
        if (likeMember == null || !likeMember){
            repository.incrementLike(category, postId);
            repository.likeMember(memberId, category, postId);
            return true;
        }
        else{
            log.info("이미 좋아요를 누름" );
            return false;
        }
    }

    public Resource sendFile(String filename, FileDTO dto) throws MalformedURLException {
        String fullPath = fileStore.getFullPath(dto.getCategory(), dto.getId(), dto.getTitle(), filename);
        return new UrlResource("file:" + fullPath);
    }
}
