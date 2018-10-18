package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description Repository for book status
 * @date Created in 8:00 PM. 10/17/2018
 * @modify By 毛文杰
 */
@Repository
public interface BookStatusRepository extends JpaRepository<BookStatus, Integer> {
}
