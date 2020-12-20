package com.salesmanager.shop.store.security;

import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.shop.store.security.common.CustomAuthenticationManager;
import com.salesmanager.shop.utils.GeoLocationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final static String BEARER_TOKEN ="Bearer ";
    private final static String FACEBOOK_TOKEN ="FB ";

    private final CustomAuthenticationManager jwtCustomerAuthenticationManager;
    private final CustomAuthenticationManager jwtAdminAuthenticationManager;

	@Value("${authToken.header}")
	private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	String origin = "*";

    	if(!StringUtils.isBlank(request.getHeader("origin"))) {
    		origin = request.getHeader("origin");
    	}
    	//in flight
    	response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
    	response.setHeader("Access-Control-Allow-Origin", origin);
    	response.setHeader("Access-Control-Allow-Headers", "X-Auth-Token, Content-Type, Authorization, Cache-Control, X-Requested-With");
    	response.setHeader("Access-Control-Allow-Credentials", "true");

    	try {
    		String ipAddress = GeoLocationUtils.getClientIpAddress(request);
    		UserContext userContext = UserContext.create();
    		userContext.setIpAddress(ipAddress);
    	} catch(Exception s) {
    		LOGGER.error("Error while getting ip address ", s);
    	}

    	if(request.getRequestURL().toString().contains("/api/v1/auth")) {
    		//setHeader(request,response);   	
	    	final String requestHeader = request.getHeader(this.tokenHeader);//token
	    	
	    	try {
		        if (requestHeader != null && requestHeader.startsWith(BEARER_TOKEN)) {//Bearer

					jwtCustomerAuthenticationManager.authenticateRequest(request, response);
	
		        } else if(requestHeader != null && requestHeader.startsWith(FACEBOOK_TOKEN)) {
		        	//Facebook
		        	//facebookCustomerAuthenticationManager.authenticateRequest(request, response);
		        } else {
		        	LOGGER.warn("couldn't find any authorization token, will ignore the header");
		        }
	        
	    	} catch(Exception e) {
	    		throw new ServletException(e);
	    	}
    	}
    	
    	if(request.getRequestURL().toString().contains("/api/v1/private")) {
    		
    		//setHeader(request,response);
    		Enumeration<String> headers = request.getHeaderNames();

    		while(headers.hasMoreElements()) {
    			LOGGER.debug(headers.nextElement());
    		}

	    	final String requestHeader = request.getHeader(this.tokenHeader);//token

	    	try {
		        if (requestHeader != null && requestHeader.startsWith(BEARER_TOKEN)) {//Bearer
					jwtAdminAuthenticationManager.authenticateRequest(request, response);
		        } else {
		        	LOGGER.warn("couldn't find any authorization token, will ignore the header, might be a preflight check");
		        }
	    	} catch(Exception e) {
	    		throw new ServletException(e);
	    	}
    	}

        chain.doFilter(request, response);
        postFilter(request, response, chain);
    }

    private void postFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	try {
    		UserContext userContext = UserContext.getCurrentInstance();
    		if(userContext!=null) {
    			userContext.close();
    		}
    	} catch(Exception s) {
    		LOGGER.error("Error while getting ip address ", s);
    	}
    }
}
