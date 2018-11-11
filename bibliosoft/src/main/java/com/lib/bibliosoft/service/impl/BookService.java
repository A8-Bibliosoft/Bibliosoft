package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.entity.BookPosition;
import com.lib.bibliosoft.repository.BookPositionRepository;
import com.lib.bibliosoft.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
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

    @Autowired
    private BookPositionRepository bookPositionRepository;

    private Logger logger = LoggerFactory.getLogger(BookService.class);

    /**
     * Test
     * Throw an exception to the BookController for processing
     * @param id
     * @throws Exception
     */
//    public void getPrice(Integer id) throws Exception {
//        Book book = bookRepository.findById(id).get();
//        float price = book.getBookPrice();
//        if (price < 10){
//            throw new BiblioException(ResultEnum.TOO_SMALL);
//        }else if(price > 60){
//            throw new BiblioException(ResultEnum.TOO_LARGE);
//        }
//
//    }

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
     * @return List
     * @Author maowenjie
     * @Date: 9:39 PM. 10/8/2018
     */
    public List<Book> searchBookByNameOrAddTime(String bookname, String bookaddtime) throws ParseException {
        Date date = null;
        if(bookname == null || "".equals(bookname)){
            if (bookaddtime == null || "".equals(bookaddtime)){
                return null;
            }else{
                date = Date.valueOf(bookaddtime);
                return bookRepository.findByRegisterTime(date);
            }
        }
        if (bookaddtime == null || "".equals(bookaddtime)){
            return  bookRepository.findByBookNameLike('%' + bookname + '%');
        }else{
            return  bookRepository.searchBookByNameOrRegisterTime('%'+bookname+'%', bookaddtime);
        }
    }


    /**
     * @title BookService.java
     * @param [id, bookName, bookPosition, isbn, bookId, fprice, author, istatus]
     * @return void
     * @author 毛文杰
     * @method name updateBook
     * @date 3:13 PM. 10/14/2018
     */
    @Transactional
    public void updateBook(Integer id, String bookName, Integer bookPosition, String isbn, Integer bookId, float fprice, String author, Integer istatus) {

        Book book = bookRepository.getOne(id);
        BookPosition bookposition = bookPositionRepository.findById(bookPosition).get();
        book.setBookName(bookName);
        book.setBookIsbn(isbn);
        book.setBookId(bookId);
        book.setBookPrice(fprice);
        book.setBookAuthor(author);
        book.setBookStatus(istatus);
        book.setBookPosition(bookposition);
        bookposition.getBooks().add(book);
        bookRepository.save(book);
        //bookRepository.updateBook(id, bookName, bookPosition, isbn, bookId, fprice, author, istatus);
    }

    /**
     * @title BookService.java
     * @param [book]
     * @return void
     * @author 毛文杰
     * @method name addBook
     * @date 3:13 PM. 10/14/2018
     */
    public void addBook(Book book) {
        bookRepository.save(book);
    }

    /**
     * @title BookService.java
     * @param [bookid]
     * @return java.lang.String
     * @author 毛文杰
     * @method name findsstatusByid
     * @date 3:14 PM. 10/14/2018
     */
    public String findsstatusByid(Integer bookid) {
        return bookRepository.getsstatusByid(bookid);
    }


    /**
     * 分页按照书籍名字或者日期查询
     * @param page
     * @param size
     * @param bookname
     * @param bookaddtime
     * @return
     */
    public Page<Book> findbookbynameortime(Integer page, Integer size, String bookname, String bookaddtime) {
        Date date = null;
        if(bookname == null || "".equals(bookname)){
            if (bookaddtime == null || "".equals(bookaddtime)){
                return null;
            }else{
                date = Date.valueOf(bookaddtime);
                Pageable pageable = PageRequest.of(page-1, size, Sort.Direction.DESC, "registerTime");
                Date finalDate = date;
                Page<Book> book = bookRepository.findAll(new Specification<Book>(){
                    @Override
                    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        Predicate p1 = criteriaBuilder.equal(root.get("registerTime"), finalDate);
                        query.where(criteriaBuilder.and(p1));
                        return query.getRestriction();
                    }
                },pageable);
                return book;
            }
        }
        if (bookaddtime == null || "".equals(bookaddtime)){
            Pageable pageable = PageRequest.of(page-1, size, Sort.Direction.DESC, "registerTime");
            Page<Book> book = bookRepository.findAll(new Specification<Book>(){
                @Override
                public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Predicate p1 = criteriaBuilder.like(root.get("bookName"), '%' + bookname + '%');
                    query.where(criteriaBuilder.and(p1));
                    return query.getRestriction();
                }
            },pageable);
            return book;
//            return  bookRepository.findByBookNameLike('%' + bookname + '%');
        }else{
            date = Date.valueOf(bookaddtime);
            List<Predicate> predicatesList = new ArrayList<Predicate>();
            Pageable pageable = PageRequest.of(page-1, size, Sort.Direction.DESC, "registerTime");
            Date finalDate1 = date;
            Page<Book> book = bookRepository.findAll(new Specification<Book>(){
                @Override
                public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Predicate p1 = criteriaBuilder.like(root.get("bookName"), '%' + bookname + '%');
                    Predicate p2 = criteriaBuilder.equal(root.get("registerTime"), finalDate1);
                    //这里使用and，两条件必须同时成立
                    Predicate p3 =  criteriaBuilder.and(p1,p2);
                    query.where(criteriaBuilder.and(p3));
                    return query.getRestriction();
                }
            },pageable);
            return book;
//            return  bookRepository.searchBookByNameOrRegisterTime('%'+bookname+'%', bookaddtime);
        }

    }

    /**
     * isbn搜索分页
     * @param currpage
     * @param pagesize
     * @param isbn
     * @return
     */
    public Page<Book> getPagebyIsbn(Integer currpage, Integer pagesize, String isbn) {
        Pageable pageable = new PageRequest(currpage-1, pagesize, Sort.Direction.ASC, "registerTime");
        Page<Book> books = bookRepository.findAll(new Specification<Book>(){
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p1 = criteriaBuilder.equal(root.get("bookIsbn").as(String.class),isbn);
                query.where(criteriaBuilder.and(p1));
                return query.getRestriction();
            }
        },pageable);
        return books;
    }
}
