package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTypeRepository extends JpaRepository<BookType,Integer>{
    List<BookType> findAll();

}
