package com.salesmanager.shop.admin.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Data
public class UserAuthenticationSuccessHandler extends AbstractAuthenticatinSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void redirectAfterSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        redirectStrategy.sendRedirect(request, response, "/admin/home.html");
    }
}
