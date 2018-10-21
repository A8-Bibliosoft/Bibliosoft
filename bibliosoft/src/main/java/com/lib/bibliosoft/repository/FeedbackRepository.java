package com.lib.bibliosoft.repository;


import com.lib.bibliosoft.entity.Feedback;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 1:47 PM. 10/4/2018
 * @Modify By:
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer>, JpaSpecificationExecutor {

    @Transactional
    @Modifying
    @Query(value = "update feedback set is_view='yes' where id=?", nativeQuery = true)
    void updateStatusById(Integer id);

    List<Feedback> findFeedbacksByIsView(String yesorno);

}

