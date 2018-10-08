package com.lib.bibliosoft;

import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.service.impl.BookService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: 毛文杰
 * @Description: Unit Test
 * @Date: Created in 2:23 PM. 9/28/2018
 * @Modify By:
 */
@RunWith(SpringRunner.class)//Run in spring
@SpringBootTest//Run whole Spring Project
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    public void findOneTest() throws Exception {
        Book book = bookService.getBookById(4);
        System.out.println(book.getBookIsbn());
        Assert.assertEquals("534553", book.getBookIsbn());
    }
}
