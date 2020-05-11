package com.spring.cloud.service.impl;

import com.spring.cloud.entity.User;
import com.spring.cloud.repository.UserRepository;
import com.spring.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean hasSameUserName(Integer id, String username) {
        if (id !=null) {
            return userRepository.countOfUsername(id,username) > 0;
        }
        return userRepository.countOfUsername(username) > 0;
    }

    @Override
    public Page<User> findUserList(String username,Pageable pageable) {
        pageable.getSort().and(Sort.by(Sort.Order.desc("createDate")));
        return userRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            if (StringUtils.hasText(username)) {
                predicates.add(criteriaBuilder.equal(root.get("username"), username));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
