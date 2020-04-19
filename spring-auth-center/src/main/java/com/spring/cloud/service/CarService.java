package com.spring.cloud.service;

import com.spring.cloud.entity.Car;
import com.spring.cloud.entity.Driver;
import com.spring.cloud.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarService {
    Page<Car> loadCarsByPage(String cardNumber, Status status, int userId, int importId,Pageable pageable);

    void importDriver(List<Car> cars);

    List<Car> loadCars(String cardNumber, Status status, int userId,int importId);

    void clear();

    int batchQuery(int importId);
}
