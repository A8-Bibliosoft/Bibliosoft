package com.lib.bibliosoft.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 8:55 PM. 10/25/2018
 * @modify By 毛文杰
 */
public class Series<T> {

    private String name;

    private String type;

    private List<T> data = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Series() {
    }

    public Series(String name, String type, List<T> data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
