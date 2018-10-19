package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookTypeRepository extends JpaRepository<BookType,Integer>{
    List<BookType> findAll();

    List<BookType> findByTypeName(String categoryname);

    @Transactional
    @Modifying
    @Query(value = "update booktype set type_name=?1 where type_id=?2", nativeQuery = true)
    void updateTypeById(String typename, Integer id);
}
