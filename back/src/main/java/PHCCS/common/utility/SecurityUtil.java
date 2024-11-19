package PHCCS.common.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtil {

    // 현재 인증 객체 가져오기
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // MemberId 가져오기
    public static Long getUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        // principal이 Long 타입으로 저장되었으므로 직접 캐스팅
        return (Long) authentication.getPrincipal();
    }

    // 단일 권한(Role) 가져오기
    public static String getUserRole() {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return null;
        }

        // 단일 권한 시스템에서 첫 번째 권한만 반환
        // 권한은 "ROLE_MEMBER", "ROLE_VET", "ROLE_ADMIN" 으로 반환
        return authentication.getAuthorities().stream()
                .findFirst() // 첫 번째 권한 가져오기
                .map(GrantedAuthority::getAuthority) // 권한 이름 추출
                .orElse(null);
    }

    // 특정 권한(Role) 확인
    public static boolean hasRole(String role) {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }

}