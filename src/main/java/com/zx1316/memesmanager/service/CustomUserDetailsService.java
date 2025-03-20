package com.zx1316.memesmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Value("${my.admin.username}")
    private String adminUsername;

    @Value("${my.admin.password}")
    private String adminPassword;

    @Value("${my.user.username}")
    private String userUsername;

    @Value("${my.user.password}")
    private String userPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (adminUsername.equals(username)) {
            return new User(adminUsername, adminPassword, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }
        if (userUsername.equals(username)) {
            return new User(userUsername, userPassword, AuthorityUtils.createAuthorityList("ROLE_USER"));
        }
        throw new UsernameNotFoundException("Username not found");
    }
}
