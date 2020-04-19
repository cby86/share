package com.spring.cloud.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * "ID": "51010900000638",
 "dah": null,
 "ssdq": "51010000",
 "ssxs": "51010900",
 "clpzh": "川AD29162",
 "cpys": 14,
 "yhmc": "四川亚天科技股份有限公司",
 "yhdz": "成都高新区二环路南三段26号",
 "yy_jyfw": "网络预约出租汽车客运",
 "clcp": "长安牌",
 "cpxh": "SC7003ACBEV",
 "cx": 5,
 "csys": "白色",
 "fdjh": "JD1J000058",
 "vin": "LS5A2AJX7JA000358",
 "rllx": 6,
 "hdzkw": null,
 "zcrq": null,
 "nszt": null,
 "InsuranceEnterprise": null,
 "InsuranceID": null,
 "InsuranceType": "0                                                 ",
 "InsuranceAmount": null,
 "InsuranceBeginTime": null,
 "InsuranceEndTime": null,
 "yjlx": null,
 "fdjpl": "0",
 "xslc": 80,
 "jxzt": null,
 "xcnjsj": null,
 "yy_dlyszh": "0001649",
 "fzjg": null,
 "jyqy": null,
 "yy_yxqq": null,
 "yy_yxqz": null,
 "jdrq": 20180720,
 "Dysbxlh": null,
 "GpsPp": "博实结",
 "GpsXh": "BSJ-A6-BD",
 "GpsAzrq": "2018-07-02 00:00:00",
 "Gpsfws": 1,
 "GpsDwms": 3,
 "yy_yyzt": 2,
 "UserID": "4             ",
 "scbz": 0,
 "cc": 4620,
 "ck": 1820,
 "cg": 1540,
 "Xcnssj": null,
 "yy_fzsj": null,
 "bz": null,
 "GpsBh": "13301270434",
 "AduitStatus": 1,
 "GpsType": null,
 "SfVideo": 2,
 "Gzsj": 20180702,
 "sfbjzz": 1,
 "fwpt": "51010900006980",
 "clkw": 5,
 "ccrq": 20180117,
 "clxz": 3,
 "jsdj": 0,
 "jsdjyxrq": null,
 "jsdjpdrq": null,
 "nsjg": 0,
 "nsrq": null,
 "nsyxrq": null,
 "appId": null,
 "lxdh": "13881767123",
 "zwssxs": "高新区"
 */
@Entity
@Table(name = "car")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class Car extends IntIdBaseEntity{
    String carNumber;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    String area;

    /**
     * csys
     */
    String color;

    /**
     * yhmc
     */
    String company;

    /**
     * yhdz
     */
    String address;

    /**
     * clcp
     */
    String band;


    /**
     * cpxh
     */
    String cpxh;

    /**
     * cx
     */
    String seat;

    /**
     *
     */
    String fdjh;

    String vin;

    String rllx;

    String yy_dlyszh;
    String yy_jyfw;

    String jdrq;

    String GpsPp;

    String AduitStatus;
    String Gzsj;
    String SfVideo;
    String sfbjzz;
    String fwpt;
    String cc;
    String ck;
    String cg;
    String lxdh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_id")
    private ImportRecord importRecord;

    public void syncData(JSONObject data) {
        this.carNumber = data.getString("clpzh");
        this.area = data.getString("zwssxs");
        this.color = data.getString("csys");
        this.company = data.getString("yhmc");
        this.address = data.getString("yhdz");
        this.band = data.getString("clcp");
        this.seat = data.getString("cx");
        this.fdjh = data.getString("fdjh");
        this.vin = data.getString("vin");
        this.rllx = data.getString("rllx");
        this.yy_dlyszh = data.getString("yy_dlyszh");
        this.jdrq = data.getString("jdrq");
        this.GpsPp = data.getString("GpsPp");
        this.AduitStatus = data.getString("AduitStatus");
        this.Gzsj = data.getString("Gzsj");
        this.SfVideo = data.getString("SfVideo");
        this.sfbjzz = data.getString("sfbjzz");
        this.fwpt = data.getString("fwpt");
        this.cc = data.getString("cc");
        this.ck = data.getString("ck");
        this.cg = data.getString("cg");
        this.lxdh = data.getString("lxdh");
        this.yy_jyfw = data.getString("yy_jyfw");
        this.status = Status.PROCCED;
    }
}
