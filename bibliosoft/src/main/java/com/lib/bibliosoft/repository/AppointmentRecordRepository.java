package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.AppointmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AppointmentRecordRepository extends JpaRepository<AppointmentRecord, Integer> {
    List<AppointmentRecord> findByReaderId(String readerId);

    @Transactional
    @Modifying
    @Query(value = "insert into appointmentrecord(book_id, reader_id, lasttime) VALUES (?1,?2,?3)",nativeQuery = true)
    void insertAppointment(Integer bookId, String readerId, float lasttime);

    @Transactional
    @Modifying
    @Query(value = "update appointmentrecord set lasttime=lasttime-1 where lasttime>0",nativeQuery = true)
    void minusLasttime();

    @Transactional
    @Modifying
    @Query(value = "delete from appointmentrecord where lasttime=0",nativeQuery = true)
    void clearLasttime();

    @Transactional
    @Query(value = "select * from appointmentrecord where lasttime=0",nativeQuery = true)
    List<AppointmentRecord> findAllEnd();

    @Transactional
    @Query(value = "select * from appointmentrecord where book_id=?1",nativeQuery = true)
    List<AppointmentRecord> findbook(Integer bookId);

    @Transactional
    @Modifying
    @Query(value = "delete from appointmentrecord where book_id=?1",nativeQuery = true)
    void clearbook(Integer bookId);
}

