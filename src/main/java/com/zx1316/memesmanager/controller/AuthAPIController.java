package com.zx1316.memesmanager.controller;

import com.zx1316.memesmanager.component.JwtUtil;
import com.zx1316.memesmanager.controller.pojo.AuthenticationRequest;
import com.zx1316.memesmanager.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthAPIController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping
    public String createAuthenticationToken(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        if (!userDetails.getPassword().equals(authenticationRequest.getPassword())) {
            throw new AuthorizationDeniedException("Invalid credentials");
        }
        return jwtUtil.createToken(userDetails);
    }
}
