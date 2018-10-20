package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 *@Title: BookRepository.java
 *@Author: 毛文杰
 *@Description:
 *@Date: 5:52 PM. 9/26/2018
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    /**
     * 插入isbn
     */
    @Transactional
    @Modifying
    @Query(value = "update book set book_isbn = ?1 where book_id=?2",nativeQuery = true)
    void insertBookIsbn(String bookIsbn,Integer BookId);
    /**
     * Expansion: find books by it's ISBN
     */
    List<Book> findBookByBookIsbn(String bookIsbn);

    /**
     * experience: When we use the ‘like’ in the API or write the native SQL statement,
     *             we must first splicing the ‘%’ and then passing the parameters.
     * @param bookname
     * @param registertime
     * @return List
     */
    @Query(value = "select * from book where book_name like ?1 and register_time = ?2", nativeQuery = true)
    List<Book> searchBookByNameOrRegisterTime(String bookname, String registertime);

    /**
     * find books by book name
     * @param bookname
     * @return
     */
    List<Book> findByBookNameLike(String bookname);

    /**
     * find book only by date
     * @param registertime
     * @return
     */
    List<Book> findByRegisterTime(Date registertime);

    Book findBookByBookId(Integer bookId);

    /**
     * update a book's all info
     * @param id
     * @param bookName
     * @param bookPosition
     * @param isbn
     * @param bookId
     * @param fprice
     * @param author
     * @param istatus
     */
    @Transactional
    @Modifying
    @Query(value = "update book b set b.book_name=?2, b.book_position=?3, b.book_isbn=?4, b.book_id=?5, b.book_price=?6, b.book_author=?7, b.book_status=?8 where b.id=?1", nativeQuery = true)
    void updateBook(Integer id, String bookName, String bookPosition, String isbn, Integer bookId, float fprice, String author, Integer istatus);

    @Query(value = "select bs.desc from bookstatus bs, book b where bs.status = b.book_status and b.id = ?1", nativeQuery = true)
    String getsstatusByid(Integer bookid);


    //Reader huhao
    Book findByBookId(Integer bookId);

    Integer countAllByBookStatusAndBookIsbn(Integer bookStatus,String bookIsbn);

    List<Book> findByBookStatusAndBookIsbn(Integer bookStatus,String bookIsbn);

    List<Book> findByBookStatus(Integer status);

    @Transactional
    @Modifying
    @Query(value = "update book set book_status = ?1 where book_id = ?2",nativeQuery = true)
    void updateBookStatus(Integer bookStatus, Integer bookId);

    @Query(value = "select MAX(book_id) from book where book_isbn = ?1", nativeQuery = true)
    Integer getMaxBookIdNow(String isbn);

    //获取书籍状态
    @Query(value = "select book_status from book where book_id = ?1", nativeQuery = true)
    Integer getBookStatusByBookId(Integer bookId);

    @Query(value = "select * from book where bookposition_id=?1", nativeQuery = true)
    List<Book> findByBookPosition(Integer id);
}

