package com.spring.cloud.controller.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.cloud.base.Command;
import com.spring.cloud.entity.Driver;
import com.spring.cloud.entity.Status;
import com.spring.cloud.utils.SecurityUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DriverCommand implements Command<Driver> {
    private String cardNumber;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date createDate;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date firstDate;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date beginDate;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;
    private String name;
    private String fullName;
    private String driverName;
    private String area;
    private Status status;

    private String mobile;

    private String company;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date driverCardFirstDate;

    private String driverCarType;

    @Override
    public Driver toDomain() {
        Driver driver = new Driver();
        driver.setCardNumber(cardNumber);
        driver.setFirstDate(firstDate);
        driver.setBeginDate(beginDate);
        driver.setEndDate(endDate);
        driver.setName(name);
        driver.setUser(SecurityUtils.currentUser());
        driver.setStatus(status);
        driver.setArea(area);
        return driver;
    }

    @Override
    public Command<Driver> fromDomain(Driver domain) {
        this.area = domain.getArea();
        this.cardNumber = domain.getCardNumber();
        this.firstDate = domain.getFirstDate();
        this.beginDate = domain.getBeginDate();
        this.endDate = domain.getEndDate();
        this.status = domain.getStatus();
        this.name = domain.getName();
        this.createDate = domain.getCreateDate();
        this.driverName = domain.getDriverName();
        this.fullName = domain.getFullName();
        this.mobile = domain.getMobile();
        this.company = domain.getCompany();
        this.driverCardFirstDate = domain.getDriverCardFirstDate();
        this.driverCarType = domain.getDriverCarType();
        return this;
    }
}
