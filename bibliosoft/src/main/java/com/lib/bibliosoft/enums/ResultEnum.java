package com.lib.bibliosoft.enums;

/**
 * @Author: 毛文杰
 * @Description: Manage the error code and information uniformly
 * @Date: Created in 2:04 PM. 9/28/2018
 * @Modify By:
 */
public enum ResultEnum {
    UNKNOWN_ERROR(-1, "未知错误"),
    SUCCESS(0, "成功"),
    TOO_SMALL(100, "价格太低"),
    TOO_LARGE(101, "价格太高"),
    ;
    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
