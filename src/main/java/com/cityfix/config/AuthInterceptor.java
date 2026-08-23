package com.cityfix.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // 1. Allow static resources & explicitly public endpoints
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/") 
                || uri.startsWith("/uploads/") || uri.equals("/login") || uri.equals("/register") 
                || uri.equals("/admin/login") || uri.equals("/") 
                || uri.startsWith("/api/auth/") || uri.startsWith("/api/complaints/public/")
                || uri.startsWith("/api/complaints/images/") || uri.startsWith("/api/complaints/track/")) {
            return true;
        }

        // 2. Admin Pages & Admin APIs
        if (uri.startsWith("/admin/") || uri.equals("/admin") || uri.startsWith("/api/admin/")) {
            if (session != null && "ADMIN".equals(session.getAttribute("ROLE"))) {
                return true;
            }
            if (uri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized. Admin login required.\"}");
            } else {
                response.sendRedirect("/admin/login");
            }
            return false;
        }

        // 3. Citizen Pages & Citizen APIs
        boolean isCitizenPage = uri.equals("/dashboard") || uri.startsWith("/dashboard/")
                || uri.equals("/report-issue") || uri.startsWith("/report-issue/")
                || uri.equals("/my-complaints") || uri.startsWith("/my-complaints/")
                || uri.startsWith("/complaints/") || uri.equals("/complaints")
                || uri.equals("/profile") || uri.startsWith("/profile/")
                || uri.startsWith("/citizen/");

        boolean isCitizenApi = uri.startsWith("/api/complaints") || uri.startsWith("/api/profile");

        if (isCitizenPage || isCitizenApi) {
            if (session != null && ("CITIZEN".equals(session.getAttribute("ROLE")) || "ADMIN".equals(session.getAttribute("ROLE")))) {
                return true;
            }
            if (uri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized. Please log in.\"}");
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        return true;
    }
}
