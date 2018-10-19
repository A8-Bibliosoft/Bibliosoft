package com.lib.bibliosoft.enums;

/**
 * @Author: 毛文杰
 * @Description: Manage the error code and information uniformly
 * @Date: Created in 2:04 PM. 9/28/2018
 * @Modify By:
 */
public enum ResultEnum {
    UNKNOWN_ERROR(-1, "unknown error"),
    SUCCESS(0, "success"),
    FAILED(-2,"failed"),
//    TOO_SMALL(100, "价格太低"),
//    TOO_LARGE(101, "价格太高"),
    ADD_BOOK_FAILED(102, "Add-book failed!"),
    ADD_BOOK_SUCCESS(103, "Add-book successful!"),
    ALSO_HAS_TYPE_ERROR(104, "already has this type!"),
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
