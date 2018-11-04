package com.lib.bibliosoft.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 毛文杰
 * @description interceptor、ddResourceHandlers and addViewControllers
 * @date Created in 8:03 PM. 9/29/2018
 * @modify By: maowenjie
 */
@Configuration
public class MySpringMVCConfiguration extends WebMvcConfigurationSupport {

    private final Logger logger = LoggerFactory.getLogger(MySpringMVCConfiguration.class);

    /**
     *@title MySpringMVCConfiguration.java
     *@param registry
     *@return void 无
     *@author 毛文杰
     *@description interceptor of custom
     *@date 11:49 AM. 10/8/2018
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        HandlerInterceptor handlerInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//                HttpSession session = request.getSession();
//                /**
//                 * 判断是否登录
//                 */
//                if (session.getAttribute("islogin")!= null){
//                    //logger.info("已登录，操作正常");
//                    return true;
//                }
//                else{
//                    logger.info("未登录，请登录后再操作");
//                    response.sendRedirect("/goHomePage");
//                    return false;
//                }
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

            }
        };
        registry.addInterceptor(handlerInterceptor).addPathPatterns("/**").excludePathPatterns("/lib_login", "/goLogin", "/goHomePage", "/bookBook", "/goSearch", "/goOut", "/search", "/goBookDetail",  "/reader_login",  "/admin_login", "/goAdminLogin", "/goHomePage","/goReaderInfo",  "/librarian_login", "/code", "/404", "/500", "/static/**");
    }

    /**
     *@title: MySpringMVCConfiguration.java
     *@author: 毛文杰
     *@description:
     *@date: 11:43 AM. 10/6/2018
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 以前要访问一个页面需要先创建个Controller控制类，再写方法跳转到页面
     * 在这里配置后就不需要那么麻烦了，直接访问http://localhost:8080/login就跳转到login.html页面了,注意默认错误页面在error文件夹下，要把地址写对
     * @param registry
     */
    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/404").setViewName("/error/404");
        registry.addViewController("/500").setViewName("/error/500");
        registry.addViewController("/lib_login").setViewName("lib_login");
        registry.addViewController("/librarian_income").setViewName("librarian_income");
        registry.addViewController("/book_grading").setViewName("book_grading");
        registry.addViewController("/reader_comment").setViewName("reader_comment");
        registry.addViewController("/system_setting").setViewName("system_setting");
        registry.addViewController("/book_category_add").setViewName("book_category_add");

        super.addViewControllers(registry);
    }

    /**
     * internationalization locale setting
     * @return
     */
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }

}

