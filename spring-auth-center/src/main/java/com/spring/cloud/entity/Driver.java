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
        String area_code = data.getString("issueorganloccode");
        String area = "成都市";
        String name = data.getString("worktypename");
        String driverName = data.getString("staffname");
        String fullName = data.getString("worktypename");
        String firstDate = data.getString("certificatefirstissuedate");
        String beginTime = data.getString("certificateissuedate");
        String endTime = data.getString("certificateexpiredate");
        String driverCarType = data.getString("quasidrivetype");
        String driverCardFirstDate = data.getString("earlydate");
        this.driverCardFirstDate=JodaTimeUtils.convertToDateTime(driverCardFirstDate, "yyyyMMdd").toDate();
        this.driverCarType = driverCarType;
        this.areaCode = area_code;
        this.area = area;
        this.name = name;
        this.driverName = driverName;
        this.fullName = fullName;
        this.firstDate = JodaTimeUtils.convertToDateTime(firstDate, "yyyyMMdd").toDate();
        this.beginDate = JodaTimeUtils.convertToDateTime(beginTime, "yyyyMMdd").toDate();
        this.endDate = JodaTimeUtils.convertToDateTime(endTime, "yyyyMMdd").toDate();
        this.status = Status.PROCCED;
    }

    public void syncDetailData(JSONObject data) {
        String mobile = data.getString("dh");
        String currentStatus = data.getString("currentStatus");
        String nextStatus = data.getString("nextStatus");
        String driverCardFirstDate = data.getString("jsz_clrq");
        String driverCarType = data.getString("jsz_zjcx");
        String company = data.getString("jgmc");
        String area = data.getString("dqmc");
        this.area = area;
        this.mobile = mobile;
        this.currentStatus = currentStatus;
        this.nextStatus = nextStatus;
        this.driverCardFirstDate=JodaTimeUtils.convertToDateTimes(driverCardFirstDate).toDate();
        this.driverCarType = driverCarType;
        this.company = company;
    }
}
