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

    @Query(value = "select num from booksort where type_id = ?1 order by type_id asc",nativeQuery = true)
    List<Integer> findBookNumByBookType(Integer booktypeid);

    @Query(value = "update booksort set num = num+?1 where book_isbn = ?2",nativeQuery = true)
    @Modifying
    @Transactional
    void updateBookNumByisbn(int num, String isbn);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM booksort WHERE book_isbn=?1",nativeQuery = true)
    void deleteByIsbn(String isbn);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM booksort WHERE num=0",nativeQuery = true)
    void deleteNumEquals0();
}

