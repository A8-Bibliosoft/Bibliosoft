package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 6:08 PM. 10/20/2018
 * @modify By 毛文杰
 */
@Repository
public interface BookPositionRepository extends JpaRepository<BookPosition, Integer> {

    List<BookPosition> findByPlace(String place);

    @Transactional
    @Modifying
    @Query(value = "update bookposition set place=?1 where id=?2", nativeQuery = true)
    void updatePositionById(String placename, Integer id);
}
