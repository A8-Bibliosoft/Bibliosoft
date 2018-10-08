package com.lib.bibliosoft.utils;

import com.lib.bibliosoft.entity.Result;
import com.lib.bibliosoft.enums.ResultEnum;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 9:05 AM. 9/28/2018
 * @Modify By:
 */
public class ResultUtil {

    /**
     *@Title: ResultUtil.java
     *@Params: Object
     *@Return: result
     *@Author: 毛文杰
     *@Description: json data returned with data when we succeeded
     *@Date: 9:12 AM. 9/28/2018
     */
    public static Result success(Object object){
        Result result = new Result();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }

    /**
     *@Title: ResultUtil.java
     *@Return: result
     *@Author: 毛文杰
     *@Description: json data returned without data when we succeeded
     *@Date: 9:12 AM. 9/28/2018
     */
    public static Result success(){
        return success(null);
    }

    /**
     *@Title: ResultUtil.java
     *@Params: code, message
     *@Return: result
     *@Author: 毛文杰
     *@Description: json data returned without data when an exception occurs
     *@Date: 9:13 AM. 9/28/2018
     */
    public static Result error(Integer code, String message){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }
}
