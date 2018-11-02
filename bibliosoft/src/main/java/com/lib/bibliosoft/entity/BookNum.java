package com.lib.bibliosoft.entity;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 8:51 PM. 11/2/2018
 * @modify By 毛文杰
 */
public class BookNum {
    private Integer value;
    private String name;

    public BookNum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
