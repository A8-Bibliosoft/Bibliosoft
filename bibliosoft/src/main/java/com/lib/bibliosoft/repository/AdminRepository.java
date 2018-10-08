package com.lib.bibliosoft.repository;

import com.lib.bibliosoft.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 董杭
 * @date ${date} ${time}
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {

    Admin findByAdminName(String name);
}
