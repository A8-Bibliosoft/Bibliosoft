package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookSort;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<BookSort> findByBookIsbn(String bookIsbn);

    List<BookSort> findByBookNameLike(String bookName);

    List<BookSort> findByBookAuthorLike(String bookAuthor);

    List<BookSort> findByTypeId(Integer typeId);
}

