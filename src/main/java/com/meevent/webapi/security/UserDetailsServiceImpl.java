package com.meevent.webapi.security;

import com.meevent.webapi.model.User;
import com.meevent.webapi.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return new UserDetailsImpl(user);
    }
}
