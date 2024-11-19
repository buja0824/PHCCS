package PHCCS.service.post;

import PHCCS.common.exception.BadRequestEx;
import PHCCS.common.exception.InternalServerEx;
import PHCCS.common.file.UploadFile;
import PHCCS.common.file.FileStore;
import PHCCS.common.file.FileDTO;


import PHCCS.service.member.repository.MemberRepository;
import PHCCS.service.post.dto.*;
import PHCCS.service.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void save(Long memberId, PostDTO dto, List<MultipartFile> imageFiles, List<MultipartFile> videoFiles) throws IOException {

        String storedDir = null;
        List<UploadFile> storeImgs = null;
        List<UploadFile> storeVids = null;
        log.info("createPost()");
        log.info("dto: {}", dto);
        log.info("imageFiles: {}", imageFiles);
        log.info("videoFiles: {}", videoFiles);
        try {
            String nickName = memberRepository.findMemberProfileById(memberId).orElseThrow().getNickName();
            Post post = new Post();
            post.setMemberId(memberId);
            post.setCategory(dto.getCategory());
            post.setTitle(dto.getTitle());
            post.setContent(dto.getContent());
            post.setAuthor(nickName);
            post.setWriteTime(dto.getWriteTime());
//            post.setFileDir(storedDir);
//        post.setImageFiles(storeImgs);
//        post.setVideoFiles(storeVids);
            repository.save(dto.getCategory(), post);
            Long savedPostId = post.getId();
            log.info("savedPostId = {}", savedPostId);
            // 업로드한 이미지 저장
            if (imageFiles != null && !imageFiles.isEmpty()) {
                storeImgs = fileStore.storeFiles(imageFiles,
                        /*memberId*/savedPostId,
//                        dto.getTitle(),
                        dto.getCategory());
                storedDir = fileDir + dto.getCategory() + "/" + /*memberId*/savedPostId + "/" /*+ dto.getTitle() + "/"*/;
                log.info("storeImgs: {}", storeImgs);
            }

            // 업로드한 동영상 저장
            if (videoFiles != null && !videoFiles.isEmpty()) {
                storeVids = fileStore.storeFiles(videoFiles,
                        /*memberId*/savedPostId,
//                        dto.getTitle(),
                        dto.getCategory());

                storedDir = fileDir + dto.getCategory() + "/" + /*memberId*/savedPostId + "/" /*+ dto.getTitle() + "/"*/;
                log.info("storeVids: {}", storeVids);
            }
            log.info("storedDir = {}", storedDir);

            if(storedDir != null) repository.insertDir(dto.getCategory(), savedPostId, storedDir);

            if (savedPostId == null) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                throw new InternalServerEx("내부 오류 발생 게시글 등록 실패");
            }
        }catch (Exception e){
            if(storeImgs != null || storeVids != null) fileStore.deleteFiles(storedDir);
            throw e;
        }
//        return ResponseEntity.ok("게시글을 등록 하였습니다.");
    }

    @Transactional
    public Post showPost(String category, Long id){
        if(category != null && !category.isEmpty() && id != 0L){
            repository.incrementViewCount(category, id);
            Post post = repository.showPost(category, id);
            if(post == null){
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
                throw new BadRequestEx("게시글을 찾을 수 없습니다.");
            }
            String fileDir = post.getFileDir();
            if(fileDir != null) {
                List<String> fileNames = fileStore.findFiles(fileDir);
                post.setFileList(fileNames);
                post.setFileDir(fileDir);
            }
            post.setCategory(category);
//            return ResponseEntity.ok(post);
            log.info("|se|post = {}", post);
            return post;
        }else{
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을수 없습니다.");
            throw new BadRequestEx("게시글을 찾을 수 없습니다.");
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
    public void updatePost(Long memberId, String category, Long postId, PostUpdateDTO param, List<MultipartFile> imgs, List<MultipartFile> vids) throws IOException {
        log.info("|se|updatePost()");
        String storedDir = ""; // 새로운 저장 경로
//                fileDir + param.getCategory() + "/" + memberId + "/" + param.getTitle() +"/"; // 새로운 저장 경로

        String findFileDir =
                repository.findPostDir(category, postId); // 기존 저장 경로
        Post beforePost = repository.showPost(category, postId);
        log.info("|se|findFileDir = {}", findFileDir);
        if(!param.getCategory().equals(category)){ // 카테고리가 변경되는 수정일 때
            log.info("카테고리변경");
            log.info("|se|beforePost = {}",beforePost.toString());

            Post afterPost = new Post();
            afterPost.setMemberId(memberId);
            afterPost.setTitle(param.getTitle());
            afterPost.setContent(param.getContent());
            afterPost.setAuthor(beforePost.getAuthor());
            afterPost.setViewCnt(beforePost.getViewCnt());
//            afterPost.setFileDir(storedDir);
            afterPost.setWriteTime(beforePost.getWriteTime());
            afterPost.setUpdateTime(param.getModifyTime()+"");

            repository.save(param.getCategory(), afterPost);
            Long savedPostId = afterPost.getId();
            storedDir = fileDir + param.getCategory() + "/" + savedPostId + "/";
            if(storedDir != null) repository.insertDir(param.getCategory(), savedPostId, storedDir);
            log.info("savedPostId = {}", savedPostId);
            if(savedPostId <=0){
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 오류 발생 게시글 등록 실패");
                throw new InternalServerEx("내부 오류 발생 게시글 등록 실패");
            }else {
                repository.deletePost(category, memberId, postId);
                log.info("삭제 끝");
            }
        }else { // 카테고리가 동일한 수정일 때
            log.info("카테고리동일");
            repository.updatePost(memberId, postId, param, storedDir);
        }
        if(findFileDir != null){
            // 해당 게시글을 통해서 저장된 파일존재 확인
            // 해당 경로 찾아가서 파일들 삭제하기
            fileStore.deleteFiles(findFileDir);
        }
        if(imgs != null && !imgs.isEmpty()){
            fileStore.storeFiles(imgs, /*memberId*/beforePost.getId(), /*param.getTitle(),*/ param.getCategory());
        }
        if(vids != null && !vids.isEmpty()){
            fileStore.storeFiles(vids, /*memberId*/beforePost.getId(), /*param.getTitle(),*/ param.getCategory());
        }
//        return ResponseEntity.ok().body("수정완료");
    }

    @Transactional
    public void deletePost(String category, Long memberId, Long postId){
        log.info("|se|deletePost()");
        String findFileDir = repository.findPostDir(category, postId);
        log.info("findFileDir = {}", findFileDir);
        Post post = repository.showPost(category, postId);
        if(post == null) throw new BadRequestEx("게시글이 존재하지 않습니다.");

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
    public String likePost(Long memberId, String category, Long postId){
        log.info("|se|incrementLike()");

        Boolean likeMember = repository.isLikeMember(memberId, category, postId);
        log.info("게시글에 좋아요 누른 멤버인가 = {}", likeMember);
        if (likeMember == null || !likeMember){
            repository.incrementLike(category, postId);
            log.info("좋아요 올리기");
            repository.likeMember(memberId, category, postId);
            log.info("좋아요한 사람으로 추가");
            return "좋아요 올리기";
        }
        else{
            repository.decrementLike(category, postId);
            log.info("좋아요 내리기" );
            repository.unLikeMember(memberId, category, postId);
            log.info("좋아요한 사람에서 제거");
            return "좋아요 내리기";
        }
    }

    public Path getPath(String filename, FileDTO dto) throws MalformedURLException {
        String fullPath = fileStore.getFullPath(dto.getCategory(), /*dto.getMemberId()*/dto.getPostId(),/* dto.getTitle(),*/ filename);
        Path filePath = Paths.get(fullPath).normalize();
//        return new UrlResource("file:" + fullPath);
//        return new UrlResource(filePath.toUri());
        return filePath;
    }
}
