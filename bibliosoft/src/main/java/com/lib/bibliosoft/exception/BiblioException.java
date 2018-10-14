package com.lib.bibliosoft.exception;

import com.lib.bibliosoft.enums.ResultEnum;

/**
 * @author: 毛文杰
 * @date: Created in 9:57 AM. 9/28/2018
 * @modify By: maowenjie
 */
public class BiblioException extends RuntimeException{

    private Integer code;

    public BiblioException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
