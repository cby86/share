package com.spring.cloud.controller;

import com.spring.cloud.base.BaseController;
import com.spring.cloud.controller.command.CarCommand;
import com.spring.cloud.controller.command.UserCommand;
import com.spring.cloud.entity.Car;
import com.spring.cloud.entity.Status;
import com.spring.cloud.entity.User;
import com.spring.cloud.service.UserService;
import com.spring.cloud.utils.CommandUtils;
import com.spring.cloud.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends BaseController {
    @Autowired
    UserService userService;
    @RequestMapping("/findUserById")
    public Map<String, Object> findUserById(Integer id) {
        User user = userService.findUser(id);
        return this.resultMap(new UserCommand().fromDomain(user));
    }

    @RequestMapping("/findUsers")
    public Map<String, Object> findUserList(String username,Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<User> userList = userService.findUserList(username,pageable);
        return this.resultMap(CommandUtils.responsePage(userList.getTotalElements(), userList.getTotalPages(),
                CommandUtils.toCommands(userList.getContent(), UserCommand.class)));
    }

    @RequestMapping("/deleteUser")
    public Map<String, Object> deleteUser(Integer id) {
        User user = userService.findUser(id);
        if (user == null) {
            throw new UnsupportedOperationException("用户不存在");
        }
        user.setDeleted(true);
        userService.save(user);
        return this.resultMap(true);
    }

    @RequestMapping("/save")
    public Map<String, Object> save(UserCommand userCommand) {
        User user = userCommand.toDomain();
        userService.save(user);
        return this.resultMap(true);
    }

    @RequestMapping("/changePassword")
    public Map<String, Object> save(String newPassword) {
        if (!StringUtils.hasText(newPassword)) {
            throw new UnsupportedOperationException("密码不能为空");
        }
        User currentUser = SecurityUtils.currentUser();
        User user = userService.findUser(currentUser.getId());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userService.save(user);
        return this.resultMap(true);
    }
}
