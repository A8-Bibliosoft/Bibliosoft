package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 11:20 AM. 10/4/2018
 * @Modify By:
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
