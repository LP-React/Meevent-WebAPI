package com.meevent.webapi.security;

import com.meevent.webapi.model.User;
import com.meevent.webapi.model.enums.AuthProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final boolean active;
    private final AuthProvider authProvider;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.active = user.getActive();
        this.authProvider = user.getAuthProvider();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    public Long getId() {
        return id;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
