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
import org.apache.http.util.TextUtils;
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
    public Page<Car> loadCarsByPage(String cardNumber, Status status,int userId, int importId,Pageable pageable) {
        pageable.getSort().and(Sort.by(Sort.Order.desc("createDate")));
        return carRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return getPredicate(cardNumber, status, userId, root, criteriaBuilder,importId);
        }, pageable);
    }

    @Override
    public void importDriver( List<String[]> excelData, ImportRecord importRecord) {
        List<Car> cars = new ArrayList<>();
        excelData.forEach(item -> {
            Car car = new Car();
            car.setStatus(Status.WARITIN);
            car.setCarNumber(item[0]);
            if (!this.isCarnumberNO(car.getCarNumber())) {
                car.setValid(false);
            }
            car.setUser(SecurityUtils.currentUser());
            cars.add(car);
        });
        carRepository.saveAll(cars);
        importRecord.setImportCount(cars.size());
        importRecordRepository.save(importRecord);
    }


    public  boolean isCarnumberNO(String carnumber) {
   /*
   1.常规车牌号：仅允许以汉字开头，后面可录入六个字符，由大写英文字母和阿拉伯数字组成。如：粤B12345；
   2.武警车牌：允许前两位为大写英文字母，后面可录入五个或六个字符，由大写英文字母和阿拉伯数字组成，其中第三位可录汉字也可录大写英文字母及阿拉伯数字，第三位也可空，如：WJ警00081、WJ京1234J、WJ1234X。
   3.最后一个为汉字的车牌：允许以汉字开头，后面可录入六个字符，前五位字符，由大写英文字母和阿拉伯数字组成，而最后一个字符为汉字，汉字包括“挂”、“学”、“警”、“军”、“港”、“澳”。如：粤Z1234港。
   4.新军车牌：以两位为大写英文字母开头，后面以5位阿拉伯数字组成。如：BA12345。
       */
        String carnumRegex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
        if (StringUtils.isEmpty(carnumber)) return false;
        else return carnumber.matches(carnumRegex);
    }

    @Override
    public List<Car> loadCars(String cardNumber, Status status,int userId,int importId) {
        return carRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return getPredicate(cardNumber, status, userId, root, criteriaBuilder,importId);
        });
    }

    private Predicate getPredicate(String cardNumber, Status status, int userId, Root<Car> root, CriteriaBuilder criteriaBuilder,int importId) {
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
    public  int batchQuery(int importId) {
        User user = SecurityUtils.currentUser();
        User userInDb = userRepository.getOne(user.getId());
        if (userInDb.getTimes() <= 0) {
            throw new UnsupportedOperationException("次数不够，请联系管理员");
        }
        List<Car> cars = this.loadCars(null, Status.WARITIN, user.getId(),importId);
        if (cars.isEmpty()) {
            throw new UnsupportedOperationException("没有待查询的记录");
        }
        int size = cars.size();
        if (size > user.getTotalCount()) {
            throw new UnsupportedOperationException("您当前最多能批量处理"+user.getTotalCount()+"条数据,当前待处理"+size+"条,如需更多请联系管理员");
        }
        Car car = cars.get(0);
        ImportRecord importRecord = car.getImportRecord();
        cars.forEach(item->{
            try {
                String resuslt = HttpClientUtil.queryCar(item.getCarNumber(),userInDb.getReferUrl());
                JSONObject jsonObject = JSONObject.parseObject(resuslt);
                JSONArray jsonArray = jsonObject.getJSONObject("datalist").getJSONArray("TB0");
                if (jsonArray.size() > 0) {
                    JSONObject data = jsonArray.getJSONObject(0);
                    item.syncData(data);
                }
                Thread.currentThread().sleep(300);
            } catch (Exception ex) {
                log.error("查询司机错误",ex);
                item.setStatus(Status.PROCCED);
            }
            importRecord.addQueryCount();
        });
        int usedTimes;
        if (size % User.countPerTimes==0) {
            usedTimes = size /  User.countPerTimes;
        }else {
            usedTimes = size /  User.countPerTimes + 1;
        }
        importRecord.setUsedTimes(usedTimes);
        userRepository.updateTimes(usedTimes,user.getId());
        return cars.size();
    }

    public static void main(String[] args) {
        System.out.println(100 /10);
    }
}
