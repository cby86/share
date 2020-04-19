package com.spring.cloud.repository;

import com.spring.cloud.entity.Car;
import com.spring.cloud.entity.User;
import org.omg.CORBA.INTERNAL;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends BaseRepository<Car,Integer> {

    @Modifying
    @Query("update Car obj set obj.deleted=true where obj.user.id=?1 and obj.deleted=false")
    void updateCar(int userId);
}
