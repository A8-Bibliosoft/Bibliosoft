package com.lib.bibliosoft.configuration;

import com.lib.bibliosoft.service.impl.BookService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 3:41 PM. 9/29/2018
 * @Modify By:
 */
public class Main {
    //Instantiate the Sping container via java configuration
    public static void main(String args[]){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfiguration.class);

        BookService bookService = context.getBean(BookService.class);
        context.destroy();
    }
}
