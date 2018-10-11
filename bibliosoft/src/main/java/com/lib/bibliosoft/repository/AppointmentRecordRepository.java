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

    void deleteById(Integer id);
}

