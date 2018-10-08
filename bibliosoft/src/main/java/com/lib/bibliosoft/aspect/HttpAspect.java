package com.lib.bibliosoft.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author: 毛文杰
 * @Description: 切面
 * @Date: Created in 10:14 PM. 9/27/2018
 * @Modify By:
 */
@Aspect
@Component
public class HttpAspect {
    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    /**
     * 设置切面的作用地点
     */
//    @Pointcut("execution(public * com.lib.bibliosoft.controller..*(..))")
//    public void log(){
//    }
//
//    @Before("log()")
//    public void doBefore(JoinPoint joinPoint){
//        logger.info("before");
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//        HttpSession session = request.getSession();
//
//        //url
//        logger.info("url={}", request.getRequestURL());
//        //method
//        logger.info("method={}", request.getMethod());
//        //IP
//        logger.info("IP={}", request.getRemoteAddr());
//        //class method
//        logger.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//        //params
//        logger.info("args={}", joinPoint.getArgs());
//    }
//
//    @After("log()")
//    public void doAfter(){
//        logger.info("after");
//    }
//
//    @AfterReturning(returning = "object", pointcut = "log()")
//    public void adAfterReturn(Object object){
//        logger.info("response={}", object.toString());
//    }
}
