package com.lib.bibliosoft.entity;

import javax.persistence.*;

/**
 * @Author: 毛文杰
 * @Description: status of book
 * @Date: Created in 7:22 PM. 10/7/2018
 * @Modify By: maowenjie
 */
@Entity
@Table(name = "bookstatus")
public class BookStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer status;
    private String desc;

    public BookStatus() {

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
