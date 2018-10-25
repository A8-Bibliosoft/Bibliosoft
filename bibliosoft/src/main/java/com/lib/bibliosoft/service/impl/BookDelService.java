package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.entity.BookDelRecord;
import com.lib.bibliosoft.repository.BookDelRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 毛文杰
 * @project bibliosoft
 * @description
 * @date Created in 3:33 PM. 10/25/2018
 * @modify By 毛文杰
 */
@Service
public class BookDelService {

    @Autowired
    private BookDelRecordRepository bookDelRecordRepository;

    @Transactional(readOnly = true)  // 只读事务
    public Page<BookDelRecord> getPageSort(Integer currPage, Integer pagesize) {
        Sort sort = new Sort(Sort.Direction.DESC,"time");
        Pageable pageable = PageRequest.of(currPage-1, pagesize, sort);
        return bookDelRecordRepository.findAll(pageable);
    }
}
