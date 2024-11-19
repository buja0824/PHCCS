package PHCCS.common.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * fileDir/게시판의타입/게시글의 PK 가 fullPath로 생성 됩니다.
     * 그 폴더에 파일을 저장합니다. 파일 이름은 fileName
     */
    public String getFullPath(String boardType, Long /*memberId*/savedPostId,/* String postTitle,*/ String fileDir){
        return fileDir +
                boardType + "/" +
                /*memberId*/savedPostId + /*"/" + postTitle +*/ "/" +
                fileDir;
    }

    /**
     * AI이미지 검사 후 저장된 사진의 경로를 찾는 오버로딩 메소드
     */
    public String getFullPath(String fileName, Long memberId){
        return fileDir +
                "skinImg/" +
                memberId + "/" +
                fileName;
    }

    public List<UploadFile> storeFiles(
            List<MultipartFile> multipartFiles,
            Long /*memberId*/savedPostId, /*String title,*/ String boardType) throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()){
                log.info("multipartFile = {}", multipartFile);
                storeFileResult.add(storeFile(multipartFile, /*memberId*/savedPostId, /*title,*/ boardType));
            }
        }
        log.info("storeFileResult = {}", storeFileResult);
        return storeFileResult;
    }
    /**
     * originalFileName이 사진1.png 라면
     * 서버에 저장하는 파일명은 저장 폴더에 간 후
     * UUID + 파일 확장자로 한다
     */
    private UploadFile storeFile(
            MultipartFile multipartFile,
            Long /*memberId*/savedPostId, /*String postTitle,*/ String boardType) throws IOException {

        if(multipartFile.isEmpty()) return null;
        // 파일 이름.확장자
        String originalFileName = multipartFile.getOriginalFilename();
        log.info("originalFileName = {}", originalFileName);
        String storeFileName = createStoreFileName(originalFileName);
        log.info("storeFileName = {}", storeFileName);

        // 디렉터리에 저장하기
        File file = new File(getFullPath(boardType, /*memberId*/savedPostId, /*postTitle,*/ storeFileName));
        File parentFile = file.getParentFile();
        if(!parentFile.exists()) {
            if(!parentFile.mkdirs()){
                throw new IOException("파일 저장 실패");
            }
        }
        multipartFile.transferTo(file); // 파일 저장
        return new UploadFile(originalFileName, storeFileName, getFullPath(boardType, /*memberId*/savedPostId, /*postTitle,*/ storeFileName));
    }

    /**
     * 피부질환 확인 AI모델을 이용하기 위해 사용자가 전송한 사진 1장을 처리하기 위한 오버로딩 메서드
     */
    public UploadFile storeFile(
            MultipartFile image, Long memberId) throws IOException {
        String imageName = image.getOriginalFilename();
        String storeFileName = createStoreFileName(imageName);
        File file = new File(fileDir + "skinImg/"+ memberId+"/"+storeFileName);
        File parentFile = file.getParentFile();
        if(!parentFile.exists()){
            if(!parentFile.mkdirs()){
                throw new IOException("파일 저장 실패");

            }
        }
        image.transferTo(file);
        return new UploadFile(imageName, storeFileName, fileDir + "skinImg/"+ memberId+"/"+storeFileName);
    }
    public List<String> findFiles(String dir){
        List<String> fileList = new ArrayList<>();

        File fileDir = new File(dir);

        if(fileDir.exists() && fileDir.isDirectory()){
            File[] files = fileDir.listFiles();
            if(files != null){
                for (File file : files) {
                    fileList.add(file.getName());
                }
            }
        }else{
            return null;
        }
        return fileList;
    }

    /**
        @param fileDir
     게시판에서 호출 시 파일, 폴더 모두 삭제, ai 검사 부분에서 호출 시 해당 파일만 삭제
     게시판에서는 C:/spring/qna_board/1/더미_질문/ 이라는 경로만 넘어오고
     ai에서는 C:\spring\skinImg\1\81834a3c-b0ff-4aa1-b776-3474657f5a12 처럼 파일 까지 넘어옴

     */
    public void deleteFiles(String fileDir){
        File endPoint = new File(fileDir);
        if(!endPoint.exists()) {
            log.info("deleteFiles(), 삭제하려는 경로가 존재하지 않음");
            return;
        }
        log.info("endPoint = {}", endPoint);
        if(endPoint.isFile()){
            log.info("AI 검사 에서 호출 ::{}", endPoint);
            endPoint.delete();
        }
        else {
            log.info("게시판 서비스에서 호출 ::");
            File[] files = endPoint.listFiles();
            if (files == null) return;
            for (File file : files) {
                log.info("file = {}", file);
                file.delete();
            }
            endPoint.delete();
        }
    }

    private static String createStoreFileName(String originalFileName) {
        // 확장자 추출
        String ext = extractExt(originalFileName);
        // 서버에 저장할 파일 이름 만들기
        return UUID.randomUUID() + "." + ext;
    }

    private static String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }
}
