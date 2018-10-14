package com.lib.bibliosoft.utils;

import com.lib.bibliosoft.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @author 毛文杰
 * @description
 * @date Created in 10:14 AM. 10/14/2018
 * @modify By: maowenjie
 */
@Component
public class BookIdUtil {

    @Autowired
    private BookRepository bookRepository;

    private static BookIdUtil bookIdUtil;

    /**
     * @title BookIdUtil.java
     * @param []
     * @return void
     * @author 毛文杰
     * @method name init
     * @date 2:08 PM. 10/14/2018
     * 通过@PostConstruct实现初始化bean之前进行的操作
     */
    @PostConstruct
    public void init() {
        bookIdUtil = this;
        bookIdUtil.bookRepository = this.bookRepository;
        // 初使化时将已静态化的bookIdUtil实例化
    }

    /**
     * @title BookIdUtil.java
     * @param [isbn] book's isbn number
     * @return java.lang.Integer
     * @author 毛文杰
     * @method name getMaxBookId
     * @date 10:24 AM. 10/14/2018
     */
    public static Integer getMaxBookId(String isbn){
        Integer maxbookid = bookIdUtil.bookRepository.getMaxBookIdNow(isbn);
        if(maxbookid == null) {
            return -1;
        }else{
            return maxbookid;
        }
    }
}
