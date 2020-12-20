package com.salesmanager.shop.store.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class SocialCustomerServicesImpl implements UserDetailsService {

    private final UserDetailsService jwtCustomerServicesImpl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //delegates to Customer fetch service
        UserDetails userDetails = jwtCustomerServicesImpl.loadUserByUsername(username);
        if (userDetails == null) {
            return null;
        }

        return userDetails;
    }

}
