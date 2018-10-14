package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookDelRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description the repository for book_delete_record
 * @date Created in 2:10 PM. 10/14/2018
 * @modify By 毛文杰
 */
public interface BookDelRecordRepository extends JpaRepository<BookDelRecord,Integer> {

}
