package com.lib.bibliosoft.configuration;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
            //将国际化语言保存到session
            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("i18n_language_session", locale);
        } else {
            //parameter has no locale info 如果没有带国际化参数，则判断session有没有保存，有保存，则使用保存的，
            //也就是之前设置的，避免之后的请求不带国际化参数造成语言显示不对
            HttpSession session = httpServletRequest.getSession();
            Locale localeInSession = (Locale) session.getAttribute("i18n_language_session");
            if(localeInSession != null) {
                locale = localeInSession;
            }
            else
                locale = new Locale("en","US");
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}



