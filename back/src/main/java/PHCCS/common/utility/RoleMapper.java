package PHCCS.common.utility;

import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public static String mapRole(int role) {
        switch (role) {
            case 0: return "ROLE_MEMBER";
            case 1: return "ROLE_VET";
            case 2: return "ROLE_ADMIN";
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    public static int reverseMapRole(String role) {
        switch (role) {
            case "ROLE_MEMBER": return 0;
            case "ROLE_VET": return 1;
            case "ROLE_ADMIN": return 2;
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}