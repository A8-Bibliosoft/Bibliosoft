package com.lib.bibliosoft.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

//获得周月年的收益支出
@Entity
@Table(name="readermoney")
public class ReaderMoney {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String readerId;
    //一次还款增加的金额
    private Integer oncedebt;
    //首次添加为300，删除reader为-300，已存在为0
    private Integer deposit;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date adddate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public Integer getOncedebt() {
        return oncedebt;
    }

    public void setOncedebt(Integer oncedebt) {
        this.oncedebt = oncedebt;
    }

    public Date getAdddate() {
        return adddate;
    }

    public void setAdddate(Date adddate) {
        this.adddate = adddate;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }
}
