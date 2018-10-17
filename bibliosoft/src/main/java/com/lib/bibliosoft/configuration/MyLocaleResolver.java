package com.lib.bibliosoft.configuration;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @Author: 毛文杰
 * @Description: web internationalization, we can carry regional information on the link
 * @Date: Created in 5:28 PM. 10/6/2018
 * @Modify By: 毛文杰
 */
public class MyLocaleResolver implements LocaleResolver {


    /**
     * @title MyLocaleResolver.java
     * @author 毛文杰
     * @description 编写自己的区域解析器
     * @date 8:25 PM. 10/6/2018
     */
    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String l = httpServletRequest.getParameter("l");
        Locale locale = null;
        //parameter has locale info
        if (!StringUtils.isEmpty(l)) {
            String[] split = l.split("_");
            locale = new Locale(split[0], split[1]);
        } else {
            //parameter has no locale info
            locale = new Locale("en","US");
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}



