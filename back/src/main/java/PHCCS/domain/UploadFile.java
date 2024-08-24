package PHCCS.domain;

import lombok.Data;

@Data
public class UploadFile {
    private String uploadFileName; // 사용자가 업로드한 파일 이름
    private String storeFileName;  // 실제 서버에 저장한 파일 이름, 덮어쓰기 방지용 uuid 사용
    private String fileDir;

    public UploadFile(String uploadFileName, String storeFileName, String fileDir) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.fileDir = fileDir;
    }
}
