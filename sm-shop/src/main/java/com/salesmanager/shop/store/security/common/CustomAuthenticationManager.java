package com.salesmanager.shop.store.security.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract authentication manager to be used by various internal Authentication manager
 * invoked from a SecurityFilter placed in the security filter chain of given http configuration.
 *
 * @author c.samson
 */
@Slf4j
@Getter
public abstract class CustomAuthenticationManager {

    @Value("${authToken.header}")
    private String tokenHeader;

    public void authenticateRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing authentication");
        }

        Authentication authResult = null;

        try {
            authResult = this.attemptAuthentication(request, response);
            if (authResult == null) {
                // return immediately as subclass has indicated that it hasn't completed
                // authentication
                return;
            }
        } catch (AuthenticationException failed) {
            // Authentication failed
            unsuccess(request, response);
            return;
        }

        this.success(request, response, authResult);


    }

    private void success(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws AuthenticationException {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authentication success");
            LOGGER.debug("Updated SecurityContextHolder to containAuthentication");
        }

        successfullAuthentication(request, response, authentication);
    }

    private void unsuccess(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        SecurityContextHolder.clearContext();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Authentication request failed");
            LOGGER.debug("Updated SecurityContextHolder to contain null Authentication");
        }

        unSuccessfullAuthentication(request, response);
    }


    public abstract Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, Exception;

    public abstract void successfullAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws AuthenticationException;

    public abstract void unSuccessfullAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

}
