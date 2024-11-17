package PHCCS.common.response;

import PHCCS.common.exception.InternalServerEx;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ApiResponse<T> extends ResponseEntity<T> {

    public ApiResponse(HttpStatusCode status) {
        super(status);
    }

    public static ResponseEntity<?> successCreate(String message){
        return ResponseEntity.ok(message);
    }

    public static ResponseEntity<?> successCreate(){
        return ResponseEntity.ok("저장되었습니다.");
    }


    public static ResponseEntity<?> successUpdate() {
        return ResponseEntity.ok("수정되었습니다.");
    }

    public static ResponseEntity<?> successDelete() {
        return ResponseEntity.ok("삭제되었습니다.");
    }


}
