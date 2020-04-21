package com.spring.cloud.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spring.cloud.utils.JodaTimeUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@Table(name = "driver")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class Driver extends IntIdBaseEntity {
    boolean valid = true;
    private String cardNumber;
    private Date firstDate;
    private Date beginDate;
    private Date endDate;
    private String name;
    private String fullName;
    private String area;
    private String areaCode;
    private String driverName;
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String mobile;
    private String currentStatus;
    private String nextStatus;
    private Date driverCardFirstDate;
    private String company;
    private String driverCarType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id")
    private ImportRecord importRecord;

    public void syncData(JSONObject data) {
        String area_code = data.getString("ssdq");
        String area = data.getString("fzjg");
        String name = data.getString("cylb");
        String driverName = data.getString("xm");
        String fullName = data.getString("cylbfullName");
        String firstDate = data.getString("clrq");
        String beginTime = data.getString("beginTime");
        String endTime = data.getString("endTime");
        this.areaCode = area_code;
        this.area = area;
        this.name = name;
        this.driverName = driverName;
        this.fullName = fullName;
        this.firstDate = JodaTimeUtils.convertToDateTime(firstDate, "yyyy-MM-dd").toDate();
        this.beginDate = JodaTimeUtils.convertToDateTime(beginTime, "yyyy-MM-dd").toDate();
        this.endDate = JodaTimeUtils.convertToDateTime(endTime, "yyyy-MM-dd").toDate();
        this.status = Status.PROCCED;
    }

    public void syncDetailData(JSONObject data) {
        String mobile = data.getString("dh");
        String currentStatus = data.getString("currentStatus");
        String nextStatus = data.getString("nextStatus");
        String driverCardFirstDate = data.getString("jsz_clrq");
        String driverCarType = data.getString("jsz_zjcx");
        String company = data.getString("jgmc");
        this.mobile = mobile;
        this.currentStatus = currentStatus;
        this.nextStatus = nextStatus;
        this.driverCardFirstDate=JodaTimeUtils.convertToDateTimes(driverCardFirstDate).toDate();
        this.driverCarType = driverCarType;
        this.company = company;
    }
}
