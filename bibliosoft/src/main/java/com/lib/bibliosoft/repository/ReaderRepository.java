package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 *@Title: ReaderRepository.java
 *@Author: 毛文杰
 *@Description: repository, you can define what you want.
 *@Date: 5:52 PM. 9/26/2018
 */
@Repository
public interface ReaderRepository extends JpaRepository<Reader, Integer> {
    /**
     * find a reader by his name
     * @param name
     * @return
     */
    Reader findReaderByReaderName(String name);

    //找出所有的状态不是del的且注册日期时指定日期的读者
    @Query(value = "select * from reader where regist_time = ?1 and status != ?2",nativeQuery = true)
    List<Reader> searchByRegistTimeandStatusnotDel(Date day, String status);

    @Query(value = "select * from reader where year(regist_time)=?1 and month(regist_time)=?2 and status != ?3",nativeQuery = true)
    List<Reader> findByMonthRegistTimeandStatusnotDel(String year,String month, String status);

    @Query(value = "select * from reader where regist_time>=?1 and regist_time<=?2 and status != ?3",nativeQuery = true)
    List<Reader> findByWeekandStatusnotDel(Date startdate,Date enddate, String status);
    /**
     * find a reader by reader_id not id
     * @param id
     * @return
     */
    Reader findReaderByReaderId(String id);
    /**
     * find a reader or readers by reader_name or phone
     * @param string
     * @return
     */
    @Transactional
    @Query(value = "select r from Reader r where r.readerName like %?1% or r.phone like %?1%")
    List<Reader> searchReaderByPhoneOrName(String string);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.reader_name=?3, r.sex=?2, r.phone=?4, r.reader_id=?5, r.email=?6, r.status=?7, r.password=?8 where r.id = ?1", nativeQuery = true)
    void updateReader(Integer id, String sex, String readerName, String phone, String readerId, String email, String status, String password, java.util.Date registTime);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.status = ?2 where r.id = ?1", nativeQuery = true)
    void updateReaderStatusById(Integer id, String status);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.status = ?2 where r.reader_id = ?1", nativeQuery = true)
    void  updateReaderStatusByReaderId(String ReaderId, String status);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.reader_name=?3, r.sex=?2, r.imgsrc=?4 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderBasic(String readerId,String sex,String readerName,String imgsrc);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.reader_name=?3, r.sex=?2 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderBasic(String readerId,String sex,String readerName);
    //修改密码
    @Transactional
    @Modifying
    @Query(value = "update reader r set r.password=?2 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderPassword(String readerId,String password);
    //修改邮箱
    @Transactional
    @Modifying
    @Query(value = "update reader r set r.email=?2 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderEmail(String readerId,String email);
    //更新读者当前欠款信息
    @Transactional
    @Modifying
    @Query(value = "update reader r set r.alldebt=?2 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderNowDebt(String readerId,float alldebt);

    //所有欠款读者
    @Transactional
    @Query(value = "select r from Reader r where r.alldebt>0")
    List<Reader> findAllDebtReader();
}

