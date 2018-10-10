package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {

    //已还书籍
    List<BorrowRecord> findByReaderIdAndReturntimeIsNotNull(String readerId);

    //未还书籍
    List<BorrowRecord> findByReaderIdAndReturntimeIsNull(String readerId);

    //归还提醒
    List<BorrowRecord> findByReaderIdAndLastdayLessThan(String readerId, Integer lastday);


    @Transactional
    @Modifying
    @Query(value = "insert into borrowrecord(book_id, borrowtime, lastday, reader_id, returntime) VALUES (?1,?2,?3,?4,?5)",nativeQuery = true)
    void insertBorrow(Integer bookId, Date borrowtime, Integer lastday, Integer readerId, Date returntime);

    @Transactional
    @Modifying
    @Query(value = "update borrowrecord set returntime=?1,lastday=?2 where id=?3",nativeQuery = true)
    void updateBorrow(Date returntime, Integer lastday, Integer id);


}

