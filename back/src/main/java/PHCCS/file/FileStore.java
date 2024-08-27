package PHCCS.file;

import PHCCS.domain.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    /**
     * 사용자가 업로드한 파일을 저장할 디렉터리 경로 생성하는 메서드
     * 기본 경로는 fileDir입니다.
     * 사용자의 id(PK)를 폴더 경로로 생성
     * + 사용자가 업로드하는데 사용한 게시판(예를 들어, community_board)을 하위 폴더로 생성
     * fileDir/게시판의타입/사용자의PK 가 fullPath로 생성 됩니다.
     * 그 폴더에 파일을 저장합니다. 파일 이름은 fileName
     */
    public String getFullPath(String boardType, Long memberId, String postTitle, String fileName){
        return fileDir + boardType + "/" + memberId + "/" + postTitle + "/" + fileName;
    }

    public List<UploadFile> storeFiles(
            List<MultipartFile> multipartFiles,
            Long memberId, String title, String boardType) throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()){
                log.info("multipartFile = {}", multipartFile);
                storeFileResult.add(storeFile(multipartFile, memberId, title, boardType));
            }
        }
        log.info("storeFileResult = {}", storeFileResult);
        return storeFileResult;
    }
    /**
     * originalFileName이 사진1.png 라면
     * 서버에 저장하는 파일명은 저장 폴더에 간 후
     * 게시글이름, 업로드한 파일이름을 저장명으로 합니다.
     */
    public UploadFile storeFile(
            MultipartFile multipartFile,
            Long memberId, String postTitle, String boardType) throws IOException {

        if(multipartFile.isEmpty()) return null;
        // 파일 이름.확장자
        String originalFileName = multipartFile.getOriginalFilename();
        log.info("originalFileName = {}", originalFileName);
        String storeFileName = createStoreFileName(postTitle, originalFileName);
        log.info("storeFileName = {}", storeFileName);

        // 디렉터리에 저장하기
        File file = new File(getFullPath(boardType, memberId, postTitle, storeFileName));
        File parentFile = file.getParentFile();
        if(!parentFile.exists()) {
            if(!parentFile.mkdirs()){
                throw new IOException("파일 저장 실패");
            }
            multipartFile.transferTo(file); // 파일 저장
        }
        return new UploadFile(originalFileName, storeFileName, getFullPath(boardType, memberId, postTitle, storeFileName));
    }

    private static String createStoreFileName(String title, String originalFileName) {
        // 확장자 추출
        String ext = extractExt(originalFileName);
        // 서버에 저장할 파일 이름 만들기
        return originalFileName + "." + ext;
    }

    private static String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }
}
