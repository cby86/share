package com.spring.cloud.controller.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.cloud.base.Command;
import com.spring.cloud.entity.Car;
import com.spring.cloud.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CarCommand implements Command<Car> {
    boolean valid = true;
    String carNumber;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    Date createDate;
    Status status;
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

    @Override
    public Car toDomain() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Command<Car> fromDomain(Car domain) {
        this.carNumber = domain.getCarNumber();
        this.createDate = domain.getCreateDate();
        this.status = domain.getStatus();
        this.area = domain.getArea();
        this.color = domain.getColor();
        this.company = domain.getCompany();
        this.address = domain.getAddress();
        this.band = domain.getBand();
        this.cpxh = domain.getCpxh();
        this.seat = domain.getSeat();
        this.fdjh = domain.getFdjh();
        this.vin = domain.getVin();
        this.rllx = domain.getRllx();
        this.yy_dlyszh = domain.getYy_dlyszh();
        this.yy_jyfw = domain.getYy_jyfw();
        this.jdrq = domain.getJdrq();
        this.GpsPp = domain.getGpsPp();
        this.AduitStatus = domain.getAduitStatus();
        this.Gzsj = domain.getGzsj();
        this.SfVideo = domain.getSfVideo();
        this.sfbjzz = domain.getSfbjzz();
        this.fwpt = domain.getFwpt();
        this.cc = domain.getCc();
        this.ck = domain.getCk();
        this.cg = domain.getCg();
        this.lxdh = domain.getLxdh();
        this.valid = domain.isValid();
        return this;
    }
}
