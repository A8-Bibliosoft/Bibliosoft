package com.lib.bibliosoft.repository;


import com.lib.bibliosoft.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 1:47 PM. 10/4/2018
 * @Modify By:
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

}
