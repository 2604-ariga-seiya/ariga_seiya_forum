package com.example.ariga_seiya_forum.filter;

import com.example.ariga_seiya_forum.controller.form.UserForm;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(1)
@Slf4j
@Component
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();

        if (requestUri.endsWith("/login") || requestUri.contains("/css/") || requestUri.contains("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        UserForm loginUser = null;
        if (session != null) {
            loginUser = (UserForm) session.getAttribute("loginUser");
        }

        if (loginUser == null) {
            log.warn("[LoginFilter] Unauthorized access blocked. URL: {}, RemoteIP: {}",
                    requestUri, httpRequest.getRemoteAddr());

            if (session == null) {
                session = httpRequest.getSession(true);
            }
            session.setAttribute("errorMessage", "E0024");

            String contextPath = httpRequest.getContextPath();
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        log.info("[LoginFilter] Access granted. User: {}, URL: {}", loginUser.getAccount(), requestUri);

        chain.doFilter(request, response);
    }
}