package com.shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public CustomAuthenticationEntryPoint(String loginUrl) {super(loginUrl);}

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String ajaxHeader = request.getHeader("X-Requested-With");
        boolean isAjax = (ajaxHeader == null) ? false : ajaxHeader.equals("XMLHttpRequest");
        if(isAjax) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ajax Request Denied");
        }else {
            super.commence(request, response, authException);
        }
    }

}