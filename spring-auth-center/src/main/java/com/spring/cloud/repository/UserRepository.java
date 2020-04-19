package com.spring.cloud.repository;

import com.spring.cloud.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User,Integer> {
    @Query("select obj from User obj where obj.deleted=false and obj.username=?1")
    User findUserByName(String username);

    @Modifying
    @Query("update User set times=times-?1 where id=?2")
    void updateTimes(int times,int userId);
}
