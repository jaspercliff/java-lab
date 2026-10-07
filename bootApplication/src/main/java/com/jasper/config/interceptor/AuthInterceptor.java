package com.jasper.config.interceptor;

import com.jasper.config.anno.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true; 
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth methodAuth = handlerMethod.getMethodAnnotation(Auth.class);
        Auth classAuth = handlerMethod.getBeanType().getAnnotation(Auth.class);
        
        //  如果方法和类上都没有 @Auth 注解，说明是公开接口，直接放行！
        if (methodAuth == null && classAuth == null) {
            return true; 
        }

        //  如果有 @Auth 注解，说明需要鉴权，开始校验 Token
        String token = request.getHeader("Authorization");
        if (token == null || !validateToken(token)) {
            // 鉴权失败，返回 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401, \"msg\":\"未登录或Token已过期，请先登录\"}");
            return false; // 拦截请求
        }

        // 4. 鉴权成功，存入上下文
        String userId = parseUserIdFromToken(token);
        CURRENT_USER.set(userId);
        
        return true; 
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束，清理 ThreadLocal 防止内存泄漏
        CURRENT_USER.remove();
    }

    private boolean validateToken(String token) {
        return "valid_token_123".equals(token); 
    }
    private String parseUserIdFromToken(String token) {
        return "USER_888"; 
    }
}