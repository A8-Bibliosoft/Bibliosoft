package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.BorrowRecord;
import com.lib.bibliosoft.entity.Notices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
//首页公告
/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 4:21 PM. 10/18/2018
 * @modify By 毛文杰
 */
@Repository
public interface BulletinRepository extends JpaRepository<Notices, Integer> {
    //选取日期最新的3个公告放到首页
    @Transactional
    @Query(value = "select * from notices order by time desc LIMIT 3  ",nativeQuery = true)
    List<Notices> findHMNotices();
}
