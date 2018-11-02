package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookSort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *@Title: BookRepository.java
 *@Author: 毛文杰
 *@Description:
 *@Date: 5:52 PM. 9/26/2018
 */
@Repository
public interface BookSortRepository extends JpaRepository<BookSort, Integer>,JpaSpecificationExecutor<BookSort> {
    /**
     * 插入typeid
     */
    @Transactional
    @Modifying
    @Query(value = "update booksort set type_id=?1 where book_isbn =?2",nativeQuery = true)
    void insertTypeId(Integer typeId,String bookIsbn);

    List<BookSort> findByBookIsbn(String bookIsbn);

    List<BookSort> findByBookNameLike(String bookName);

    List<BookSort> findByBookAuthorLike(String bookAuthor);

    List<BookSort> findByTypeId(Integer typeId);

    //随机选择4条数据放到首页
    @Transactional
    @Query(value = "select * from booksort ORDER BY book_isbn LIMIT 4  ",nativeQuery = true)
    List<BookSort> findHMBook();

    @Query(value = "select booksort.num from booktype,booksort where booktype.type_name = ?1 and booksort.type_id = booktype.type_id",nativeQuery = true)
    List<Integer> findBookNumByBookType(String booktypename);

    @Query(value = "update num set num = num+?1 where book_isbn = ?2",nativeQuery = true)
    @Modifying
    @Transactional
    void updateBookNumByisbn(Integer num, String isbn);
}

