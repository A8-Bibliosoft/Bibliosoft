package com.lib.bibliosoft.repository;
import com.lib.bibliosoft.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Query(value = "select r from Reader r where r.readerName like %?1% or r.phone like %?1%")
    List<Reader> searchReaderByPhoneOrName(String string);


    @Transactional
    @Modifying
    @Query(value = "update reader r set r.reader_name=?3, r.sex=?2, r.phone=?4, r.reader_id=?5, r.email=?6, r.status=?7 where r.id = ?1", nativeQuery = true)
    void updateReader(Integer id, String sex, String readerName, String phone, String readerId, String email, String status);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.status = ?2 where r.id = ?1", nativeQuery = true)
    void updateReaderStatusById(Integer id, String status);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.reader_name=?3, r.sex=?2, r.imgsrc=?4 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderBasic(String readerId,String sex,String readerName,String imgsrc);

    @Transactional
    @Modifying
    @Query(value = "update reader r set r.reader_name=?3, r.sex=?2 where r.reader_id = ?1", nativeQuery = true)
    void updateReaderBasic(String readerId,String sex,String readerName);

}

