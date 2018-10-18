package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookSort;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface BookSortRepository extends JpaRepository<BookSort, Integer> {
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

}

