package com.lib.bibliosoft.handle;

import com.lib.bibliosoft.entity.Result;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.exception.BiblioException;
import com.lib.bibliosoft.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

/**
 * @Author: 毛文杰
 * @Description: Handle the Exception we threw out, Change the json output format of the thrown exception
 * @Date: Created in 9:49 AM. 9/28/2018
 * @Modify By: maoge
 */
@ControllerAdvice
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle(Exception e){
        //自定义错误类型
        if (e instanceof BiblioException){
            BiblioException biblioException = (BiblioException) e;
            return ResultUtil.error(biblioException.getCode(), biblioException.getMessage());
        }else if(e instanceof MultipartException){//上传文件错误
            MultipartException m = (MultipartException) e;
            return ResultUtil.error(m.hashCode(), m.getMessage());
        }else{
            logger.info("[系统异常]==={}", e);
            //other Exception is -1, [未知错误]
            return ResultUtil.error(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getMsg());
        }
    }
}
