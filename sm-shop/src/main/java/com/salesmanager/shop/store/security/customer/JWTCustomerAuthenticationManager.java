package com.salesmanager.shop.store.security.customer;

import com.salesmanager.shop.store.security.JwtTokenUtil;
import com.salesmanager.shop.store.security.common.CustomAuthenticationException;
import com.salesmanager.shop.store.security.common.CustomAuthenticationManager;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtCustomerAuthenticationManager extends CustomAuthenticationManager {
	
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService jwtCustomerServicesImpl;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		final String requestHeader = request.getHeader(super.getTokenHeader());//token
        String username = null;
        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {//Bearer
            authToken = requestHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
            	LOGGER.error("an error occured during getting username from token", e);
            } catch (ExpiredJwtException e) {
            	LOGGER.warn("the token is expired and not valid anymore", e);
            }
        } else {
        	throw new CustomAuthenticationException("No Bearer token found in the request");
        }
        
        UsernamePasswordAuthenticationToken authentication = null;
		
        
        LOGGER.info("checking authentication for user " + username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // It is not compelling necessary to load the use details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            UserDetails userDetails = this.jwtCustomerServicesImpl.loadUserByUsername(username);

            // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)
            if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails)) {
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                LOGGER.info("authenticated user " + username + ", setting security context");
                //SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
		
		return authentication;
	}

	@Override
	public void successfullAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unSuccessfullAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		// TODO Auto-generated method stub
	}
}
