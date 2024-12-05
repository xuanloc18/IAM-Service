package dev.cxl.iam_service.configuration;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    public static String getAuthenticatedUserName(String attribute) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null; // Nếu không có authentication
        }
        // Kiểm tra nếu xác thực là dạng AbstractAuthenticationToken
        if (authentication instanceof AbstractAuthenticationToken) {
            // Lấy thông tin principal từ token
            Object principal = ((AbstractAuthenticationToken) authentication).getPrincipal();
            // Nếu principal là JWT, lấy claim "name"
            if (principal instanceof Jwt jwt) {
                return jwt.getClaim(attribute);
            } else {
                // Debug nếu principal không phải JWT
                System.out.println("Principal is not a JWT");
            }
        }
        return null;
    }

    public static String getAuthenticatedUserID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null; // Nếu không có authentication
        }
        // Kiểm tra nếu xác thực là dạng AbstractAuthenticationToken
        if (authentication instanceof AbstractAuthenticationToken) {
            // Lấy thông tin principal từ token
            Object principal = ((AbstractAuthenticationToken) authentication).getPrincipal();
            // Nếu principal là JWT, lấy claim "name"
            if (principal instanceof Jwt jwt) {
                return jwt.getSubject();
            } else {
                // Debug nếu principal không phải JWT
                System.out.println("Principal is not a JWT");
            }
        }
        return null;
    }
}
