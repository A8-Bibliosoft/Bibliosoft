package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.entity.Notices;
import com.lib.bibliosoft.repository.BulletinRepository;
import com.lib.bibliosoft.service.IBulletinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 4:39 PM. 10/18/2018
 * @modify By 毛文杰
 */
@Service
public class BulletinService implements IBulletinService {

    @Autowired
    private BulletinRepository bulletinRepository;

    /**
     * @title BulletinService.java
     * @param currpage, pagesize
     * @return java.util.List<com.lib.bibliosoft.entity.Notices>
     * @author 毛文杰
     * @method name getPage
     * @date 4:40 PM. 10/18/2018
     */
    @Override
    public Page<Notices> getPage(Integer currpage, Integer pagesize) {
        Pageable pageable = PageRequest.of(currpage - 1, pagesize);
        return bulletinRepository.findAll(pageable);
    }
}
