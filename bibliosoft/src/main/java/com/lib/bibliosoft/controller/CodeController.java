package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.utils.VerifyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 11:07 PM. 9/30/2018
 * @Modify By:
 */
@Controller
public class CodeController {
    private static Logger logger = LoggerFactory.getLogger(CodeController.class);

    @GetMapping("/code")
    public void createCode(HttpServletRequest request, HttpServletResponse response) {

        try {
            response.setContentType("image/jpg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "no-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            VerifyCode.ran(request, response);//输出验证码图片方法
        } catch (Exception e) {
            logger.error("获取验证码失败>>>>", e);
        }
    }


}
