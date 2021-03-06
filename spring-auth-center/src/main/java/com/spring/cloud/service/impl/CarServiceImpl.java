package com.spring.cloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spring.cloud.entity.*;
import com.spring.cloud.repository.CarRepository;
import com.spring.cloud.repository.ImportRecordRepository;
import com.spring.cloud.repository.UserRepository;
import com.spring.cloud.service.CarService;
import com.spring.cloud.utils.HttpClientUtil;
import com.spring.cloud.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@Slf4j
public class CarServiceImpl implements CarService {
    @Autowired
    CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImportRecordRepository importRecordRepository;

    @Override
    public Page<Car> loadCarsByPage(String cardNumber, Status status, int userId, int importId, Pageable pageable) {
        pageable.getSort().and(Sort.by(Sort.Order.desc("createDate")));
        return carRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return getPredicate(cardNumber, status, userId, root, criteriaBuilder, importId);
        }, pageable);
    }

    @Override
    public void importDriver(List<String[]> excelData, ImportRecord importRecord, int userId) {
        User user = userRepository.getOne(userId);
        List<Car> cars = new ArrayList<>();
        List<String> error = new ArrayList<>();
        excelData.forEach(item -> {
            Car car = new Car();
            car.setStatus(Status.WARITIN);
            if (StringUtils.isEmpty(item[0]) || StringUtils.isEmpty(item[0].trim())) {
                car.setValid(false);
//                importRecord.addInvalidCount();
                error.add(item[0]);
            }else {
                car.setCarNumber(item[0].trim());
            }
            car.setUser(user);
            car.setImportRecord(importRecord);
            cars.add(car);
        });
        if (error.size() > 0) {
            throw new UnsupportedOperationException("导入数据车牌验证错误,一共" + error.size() + "条");
        }
        carRepository.saveAll(cars);
        importRecord.setImportCount(cars.size());
        importRecord.setQueryStatus(QueryStatus.READY);
        importRecordRepository.save(importRecord);
    }


    public boolean isCarnumberNO(String carnumber) {
        Pattern p = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(?:(?![A-Z]{4})[A-Z0-9]){4,6}[A-Z0-9挂学警港澳]{1}$");
        Matcher m = p.matcher(carnumber);
        if (!m.matches()) {
            return false;
        }
        return true;
    }

    @Override
    public List<Car> loadCars(String cardNumber, Status status, int userId, int importId) {
        return carRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return getPredicate(cardNumber, status, userId, root, criteriaBuilder, importId);
        });
    }

    private Predicate getPredicate(String cardNumber, Status status, int userId, Root<Car> root, CriteriaBuilder criteriaBuilder, int importId) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        Join<Object, Object> user = root.join("user", JoinType.LEFT);
        Join<Object, Object> importRecord = root.join("importRecord", JoinType.LEFT);
        if (StringUtils.hasText(cardNumber)) {
            predicates.add(criteriaBuilder.equal(root.get("carNumber"), cardNumber));
        }
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        predicates.add(criteriaBuilder.equal(user.get("id"), userId));
        predicates.add(criteriaBuilder.equal(importRecord.get("id"), importId));
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    @Override
    public void clear() {
        User user = SecurityUtils.currentUser();
        carRepository.updateCar(user.getId());
    }


    @Override
    public int batchQuery(int importId, int userId) {
        User user = userRepository.getOne(userId);
        if (user.getTimes() <= 0) {
            throw new UnsupportedOperationException("次数不够，请联系管理员");
        }
        List<Car> cars = this.loadCars(null, Status.WARITIN, user.getId(), importId);
        if (cars.isEmpty()) {
            throw new UnsupportedOperationException("没有待查询的记录");
        }
        int size = cars.size();
        if (size > user.getTotalCount()) {
            throw new UnsupportedOperationException("您当前最多能批量处理" + user.getTotalCount() + "条数据,当前待处理" + size + "条,如需更多请联系管理员");
        }
        Car car = cars.get(0);
        ImportRecord importRecord = car.getImportRecord();
        updateStatus(importRecord);
        cars.forEach(item -> {
            try {
                String resuslt = HttpClientUtil.queryCar(item.getCarNumber(), user.getReferUrl());
                JSONObject jsonObject = JSONObject.parseObject(resuslt);
                JSONArray jsonArray = jsonObject.getJSONObject("datalist").getJSONArray("TB0");
                if (jsonArray.size() > 0) {
                    JSONObject data = jsonArray.getJSONObject(0);
                    item.syncData(data);
                } else {
                    item.setValid(false);
                    importRecord.addInvalidCount();
                    item.setStatus(Status.PROCCED);
                }
                Thread.currentThread().sleep(300);
            } catch (Exception ex) {
                log.error("查询司机错误", ex);
                item.setStatus(Status.PROCCED);
            }
            importRecord.addQueryCount();
        });
        int usedTimes;
        if (size % User.countPerTimes == 0) {
            usedTimes = size / User.countPerTimes;
        } else {
            usedTimes = size / User.countPerTimes + 1;
        }
        importRecord.setUsedTimes(usedTimes);
        importRecord.setQueryStatus(QueryStatus.QUERYED);
        userRepository.updateTimes(usedTimes, user.getId());
        return cars.size();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected void updateStatus(ImportRecord importRecord) {
        importRecord.setQueryStatus(QueryStatus.QUERYING);
    }
}
