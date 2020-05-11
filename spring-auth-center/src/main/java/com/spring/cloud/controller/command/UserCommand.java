package com.spring.cloud.controller.command;

import com.spring.cloud.base.Command;
import com.spring.cloud.entity.ImportRecord;
import com.spring.cloud.entity.User;
import com.spring.cloud.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

@Setter
@Getter
public class UserCommand  implements Command<User> {
    Integer id;

    String username;

    String password;

    String realName;

    String referUrl;

    int times;

    boolean inner;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Autowired
    private UserService userService;

    @Override
    public User toDomain() {
        User user = null;
        if (id !=null) {
            user = userService.findUser(id);
        }else {
            user = new User();
        }
        if (userService.hasSameUserName(id, username)) {
            throw new UnsupportedOperationException("用户名不能重复");
        }
        if (StringUtils.hasText(password)) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            user.setPassword(bCryptPasswordEncoder.encode(password));
        }
        user.setUsername(username);
        user.setReferUrl(referUrl);
        user.setTimes(times);
        user.setRealName(realName);
        return user;
    }

    @Override
    public Command<User> fromDomain(User domain) {
        this.id = domain.getId();
        this.times = domain.getTimes();
        this.username = domain.getUsername();
        this.realName = domain.getRealName();
        this.referUrl = domain.getReferUrl();
        this.inner = domain.isSystem();
        return this;
    }
}
