package com.lib.bibliosoft;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.charset.Charset;

@Configuration
@SpringBootApplication
@EnableScheduling//开启定时任务
@EnableAsync//开启异步
public class BibliosoftApplication {

    /**
     * You can change the banner shape as you want, just add a text file named "banner.txt"
     * @title BibliosoftApplication.java
     * @author 毛文杰
     * @method name main
     * @date 7:17 PM. 9/25/2018
     */
    public static void main(String[] args) {
//        SpringApplication.run(BibliosoftApplication.class, args);
        SpringApplication springApplication = new SpringApplication(BibliosoftApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }
    //Disable the banner
//    public static void main(String args[]){
//        SpringApplication springApplication = new SpringApplication(BibliosoftApplication.class);
//        springApplication.setBannerMode(Banner.Mode.OFF);
//        springApplication.run(args);
//    }

    /**
     * Custom message converter for String to support Chinese characters
     * @title BibliosoftApplication.java
     * @author 毛文杰
     * @method name stringHttpMessageConverter
     * @date 7:17 PM. 10/1/2018
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter(){
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }
}
