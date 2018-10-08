package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.exception.BiblioException;
import com.lib.bibliosoft.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.sql.Date;
import java.util.List;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 7:35 PM. 9/26/2018
 * @Modify By:
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    private Logger logger = LoggerFactory.getLogger(BookService.class);

    /**
     * Throw an exception to the BookController for processing
     * @param id
     * @throws Exception
     */
    public void getPrice(Integer id) throws Exception {
        Book book = bookRepository.findById(id).get();
        float price = book.getBookPrice();
        if (price < 10){
            throw new BiblioException(ResultEnum.TOO_SMALL);
        }else if(price > 60){
            throw new BiblioException(ResultEnum.TOO_LARGE);
        }

    }

    /**
     * find a book by it's ID
     * @param id
     * @return
     */
    public Book getBookById(Integer id){
        return bookRepository.findById(id).get();
    }

    /**
     * find All the books
     * @return
     */
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * get all the books by paging
     * @param currpage
     * @param pagesize
     * @return
     */
    public Page<Book> getPage(Integer currpage, Integer pagesize) {
        Pageable pageable = PageRequest.of(currpage - 1, pagesize);
        return bookRepository.findAll(pageable);
    }

    /**
     * Fuzzy query about book's register time or name
     * @param bookname
     * @param bookaddtime
     * @return
     */
    public List<Book> searchBookByNameOrAddTime(String bookname, String bookaddtime) throws ParseException {
        if(bookname == null || "".equals(bookname)){
            Date date = Date.valueOf(bookaddtime);
            return bookRepository.findByRegisterTime(date);
        }
        if (bookaddtime == null || "".equals(bookaddtime)){
            return  bookRepository.findByBookNameLike('%' + bookname + '%');
        }else{
            return  bookRepository.searchBookByNameOrRegisterTime('%'+bookname+'%', bookaddtime);
        }
    }


    public void updateBook(Integer id, String bookName, String bookPosition, String isbn, Integer bookId, float fprice, String author, Integer istatus) {
        bookRepository.updateBook(id, bookName, bookPosition, isbn, bookId, fprice, author, istatus);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public String findsstatusByid(Integer bookid) {
        return bookRepository.getsstatusByid(bookid);
    }
}
