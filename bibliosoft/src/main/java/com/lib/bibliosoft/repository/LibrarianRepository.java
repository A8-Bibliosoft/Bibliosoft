package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description: JPA Repository for Librarian
 * @Date: Created in 10:41 AM. 10/4/2018
 * @Modify By:
 */
@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Integer> {
    /**
     * Expansion: find a librarian by his name
     */
    Librarian findByLibName(String name);

    Librarian findByLibId(String id);

    @Transactional
    @Modifying
    @Query(value = "update librarian lib set lib.password=?2, lib.lib_name=?3, lib.email=?4, lib.phone=?5 where lib.lib_id=?1", nativeQuery = true)
    void updateLibrarianWithPass(String id, String password, String name, String email, String phone);

    @Transactional
    @Modifying
    @Query(value = "update librarian lib set lib.lib_name=?2, lib.email=?3, lib.phone=?4 where lib.lib_id=?1", nativeQuery = true)
    void updateLibrarianWithoutPass(String id, String name, String email, String phone);

    @Override
    List<Librarian> findAll();

    @Transactional
    void deleteByLibId(String id);
}
