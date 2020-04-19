package com.spring.cloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spring.cloud.entity.*;
import com.spring.cloud.repository.DriverRepository;
import com.spring.cloud.repository.ImportRecordRepository;
import com.spring.cloud.repository.UserRepository;
import com.spring.cloud.service.DriverService;
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

@Service
@Transactional
@Slf4j
public class DriverServiceImpl implements DriverService {
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImportRecordRepository importRecordRepository;
    @Override
    public Page<Driver> loadDriversByPage(String cardNumber, Status status,int userId,int importId, Pageable pageable) {
        pageable.getSort().and(Sort.by(Sort.Order.desc("createDate")));
        return driverRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return getPredicate(cardNumber, status, userId, root, criteriaBuilder,importId);
        }, pageable);
    }

    @Override
    public void importDriver(List<Driver> drivers) {
        ImportRecord importRecord = new ImportRecord();
        importRecord.setImportCount(drivers.size());
        importRecord.setType(ImportType.DRIVER);
        importRecord.setUser(SecurityUtils.currentUser());
        importRecordRepository.save(importRecord);
        drivers.forEach(item->{
            item.setImportRecord(importRecord);
        });
        driverRepository.saveAll(drivers);
    }

    @Override
    public List<Driver> loadDrivers(String cardNumber, Status status,int userId,int importId) {
        return driverRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return getPredicate(cardNumber, status, userId, root, criteriaBuilder,importId);
        });
    }

    private Predicate getPredicate(String cardNumber, Status status, int userId, Root<Driver> root, CriteriaBuilder criteriaBuilder,int importId) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        Join<Object, Object> user = root.join("user", JoinType.LEFT);
        Join<Object, Object> importRecord = root.join("importRecord", JoinType.LEFT);
        if (StringUtils.hasText(cardNumber)) {
            predicates.add(criteriaBuilder.equal(root.get("cardNumber"), cardNumber));
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
        driverRepository.updateDriver(user.getId());
    }

    @Override
    public  int batchQuery(int importId) {
        User user = SecurityUtils.currentUser();
        User userInDb = userRepository.getOne(user.getId());
        if (userInDb.getTimes() <= 0) {
            throw new UnsupportedOperationException("次数不够，请联系管理员");
        }
        List<Driver> drivers = this.loadDrivers(null, Status.WARITIN, user.getId(),importId);
        if (drivers.isEmpty()) {
            throw new UnsupportedOperationException("没有待查询的记录");
        }
        int size = drivers.size();
        if (size > user.getTotalCount()) {
            throw new UnsupportedOperationException("您当前最多能批量处理"+user.getTotalCount()+"条数据,当前待处理"+size+"条,如需更多请联系管理员");
        }
        Driver driver = drivers.get(0);
        ImportRecord importRecord = driver.getImportRecord();
        drivers.forEach(item->{
            try {
                String resuslt = HttpClientUtil.queryDriver(item.getCardNumber(),userInDb.getReferUrl());
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
            try {
                String resuslt = HttpClientUtil.queryDriverDetails(item.getCardNumber(),userInDb.getReferUrl());
                JSONObject jsonObject = JSONObject.parseObject(resuslt);
                JSONArray jsonArray = jsonObject.getJSONObject("datalist").getJSONArray("TB0");
                if (jsonArray.size() > 0) {
                    JSONObject data = jsonArray.getJSONObject(0);
                    item.syncDetailData(data);
                }
                Thread.currentThread().sleep(300);
            } catch (Exception ex) {
                log.error("查询司机详细错误",ex);
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
        return size;
    }
}
