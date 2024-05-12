package com.snowflakes.rednose.interceptor;

import com.snowflakes.rednose.annotation.AccessibleWithoutLogin;
import com.snowflakes.rednose.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        AccessibleWithoutLogin accessibleWithoutLogin = handlerMethod.getMethodAnnotation(AccessibleWithoutLogin.class);

        // 로그인하지 않아도 되는 메서드 통과
        if (accessibleWithoutLogin != null) {
            return true;
        }

        // 로그인해야 하는 메서드에 대해 jwt 검증
        // 헤더는 Authorization: <type> <credentials> 형태이므로 토큰을 얻기 위해 split
        jwtTokenProvider.verifySignature(request.getHeader("Authorization").split(" ")[1]);
        return true;
    }
}
