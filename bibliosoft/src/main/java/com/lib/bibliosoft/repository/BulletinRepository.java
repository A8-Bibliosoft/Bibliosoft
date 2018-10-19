package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.Notices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 4:21 PM. 10/18/2018
 * @modify By 毛文杰
 */
@Repository
public interface BulletinRepository extends JpaRepository<Notices, Integer> {

}
