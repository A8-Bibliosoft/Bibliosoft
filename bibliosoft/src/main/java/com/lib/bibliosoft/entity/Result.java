package com.lib.bibliosoft.entity;

/**
 * @Author: 毛文杰
 * @Description: HTTP return object
 * @Date: Created in 8:58 AM. 9/28/2018
 * @Modify By:
 */
public class Result<T> {

    //error code
    private Integer code;

    //alarm info
    private String msg;

    //detail content
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
