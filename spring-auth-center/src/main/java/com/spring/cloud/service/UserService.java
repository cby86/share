package com.spring.cloud.service;

import com.spring.cloud.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Author panyuanjun
 * @Date 2019/11/7/007 11:03
 **/
public interface UserService extends UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String userame) throws UsernameNotFoundException;

    User findUser(int userId);
}
