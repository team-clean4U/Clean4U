package org.example.clean4u._core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionFixationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loginUser") != null) {

            if (session.getAttribute("SESSION_REGENERATED") == null) {
                request.changeSessionId();
                session.setAttribute("SESSION_REGENERATED", true);
            }
        }

        return true;
    }
}
