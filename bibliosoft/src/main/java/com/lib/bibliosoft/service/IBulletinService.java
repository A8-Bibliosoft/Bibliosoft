package com.lib.bibliosoft.service;

import com.lib.bibliosoft.entity.Notices;
import org.springframework.data.domain.Page;


/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 4:38 PM. 10/18/2018
 * @modify By 毛文杰
 */
public interface IBulletinService {

    /***
     * @title IBulletinService.java
     * @param currpage, pagesize
     * @return org.springframework.data.domain.Page<com.lib.bibliosoft.entity.Notices>
     * @author 毛文杰
     * @method name getPage 分页
     * @date 4:41 PM. 10/18/2018
     */
    Page<Notices> getPage(Integer currpage, Integer pagesize);
}
