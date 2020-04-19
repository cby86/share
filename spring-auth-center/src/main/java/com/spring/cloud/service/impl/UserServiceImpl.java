package com.spring.cloud.service.impl;

import com.spring.cloud.entity.User;
import com.spring.cloud.repository.UserRepository;
import com.spring.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByName(username);
    }

    @Override
    public User findUser(int userId) {
        return userRepository.getOne(userId);
    }
}
