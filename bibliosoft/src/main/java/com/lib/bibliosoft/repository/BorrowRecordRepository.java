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

    BorrowRecord findByBookId(Integer bookId);

    BorrowRecord findByBookIdAndReturntimeIsNull(Integer bookId);

    //个人已还记录
    List<BorrowRecord> findByReaderIdAndReturntimeIsNotNull(String readerId);

    //个人未还记录
    List<BorrowRecord> findByReaderIdAndReturntimeIsNull(String readerId);

    //个人所有欠款记录
    @Transactional
    @Query(value = "select * from borrowrecord where reader_id=?1 and borrowrecord.debt>0",nativeQuery = true)
    List<BorrowRecord> findAllDebtByReaderId(String readerId);

    //个人当前欠款总额,不包括已还的书籍
    @Transactional
    @Query(value = "select * from borrowrecord where reader_id=?1 and borrowrecord.debt>0 and returntime is null",nativeQuery = true)
    List<BorrowRecord> findNowDebtByReaderId(String readerId);

    /**
     *@Author: 毛文杰
     *@Description: find all borrow record by readerId
     *@Date: 4:09 PM. 10/12/2018
     */
    @Query(value = "select * from borrowrecord where reader_id = ?1", nativeQuery = true)
    List<BorrowRecord> findAllByReaderId(String readerId);

//    @Query(value = "select book_id from borrowrecord where reader_id = ?1", nativeQuery = true)
//    List<Integer> findBookIdByReaderId(String readerid);


    List<BorrowRecord> findByReturntime(java.sql.Date date);

    @Query(value = "select * from borrowrecord where returntime>=?1 and returntime<=?2", nativeQuery = true)
    List<BorrowRecord> findByWeek(java.sql.Date startdate, java.sql.Date enddate);

    @Query(value = "select * from borrowrecord where year(returntime)=?1 and month(returntime)=?2", nativeQuery = true)
    List<BorrowRecord> findByYearAndMonthReturntime(String year,String month);

    //所有未还记录
    List<BorrowRecord> findByReturntimeIsNull();

    //已还欠款的书的总缴纳欠款
    @Query(value = "select * from borrowrecord where returntime is not null and debt > 0", nativeQuery = true)
    List<BorrowRecord> findAllIncome();


    //所有欠款记录
    @Transactional
    @Query(value = "select * from borrowrecord where borrowrecord.debt>0",nativeQuery = true)
    List<BorrowRecord> findByDebt();

    //借书
    @Transactional
    @Modifying
    @Query(value = "insert into borrowrecord(book_id, borrowtime, lastday, reader_id) VALUES (?1,?2,?3,?4)",nativeQuery = true)
    void insertBorrow(Integer bookId, Date borrowtime, Integer lastday, String readerId);

    //还书日期
    @Transactional
    @Modifying
    @Query(value = "update borrowrecord set returntime=?1,lastday=?2 where book_id=?3 and returntime is null",nativeQuery = true)
    void updateBorrow(Date returntime, Integer lastday, Integer bookId);

    //倒计时借书日期减少
    @Transactional
    @Modifying
    @Query(value = "update borrowrecord set lastday=lastday-1 where lastday>0 and returntime is NULL",nativeQuery = true)
    void minusLastday();

    //倒计时还书提醒
    @Transactional
    @Query(value = "select * from borrowrecord where lastday=7 and returntime is NULL",nativeQuery = true)
    List<BorrowRecord> findByLastday();


    //倒计时欠款增加
    @Transactional
    @Modifying
    @Query(value = "update borrowrecord set debt=debt+1 where lastday=0 and returntime is NULL",nativeQuery = true)
    void addDebt();

    //还款清空
    @Transactional
    @Modifying
    @Query(value = "update borrowrecord set debt=0 where reader_id=?1 and returntime is NULL",nativeQuery = true)
    void clearDebt(String readerId);

    //归还提醒
    List<BorrowRecord> findByReaderIdAndLastdayLessThan(String readerId, Integer lastday);

    //书籍续借
    @Transactional
    @Modifying
    @Query(value = "update borrowrecord set lastday=?2 where book_id=?1",nativeQuery = true)
    void renew(Integer bookId,Integer lastday);

    List<BorrowRecord> findAllByBookId(Integer bookid);
}

