package com.spring.cloud.repository;

import com.spring.cloud.entity.Driver;
import com.spring.cloud.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DriverRepository extends BaseRepository<Driver,Integer> {

    @Modifying
    @Query("update Driver obj set obj.deleted=true where obj.user.id=?1 and obj.deleted=false")
    void updateDriver(int userId);
}
