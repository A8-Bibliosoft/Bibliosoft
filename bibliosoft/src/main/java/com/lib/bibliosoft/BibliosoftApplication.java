package com.lib.bibliosoft;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;

@Configuration
@SpringBootApplication
public class BibliosoftApplication {

    //You can change the banner shape as you want, just add a text file named "banner.txt"
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

    //Custom message converter for String to support Chinese characters
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter(){
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        return stringHttpMessageConverter;
    }
}
