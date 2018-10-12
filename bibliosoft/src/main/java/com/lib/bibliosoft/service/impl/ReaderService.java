package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.ReaderRepository;
import com.lib.bibliosoft.service.IReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description: service for reader
 * @Date: Created in 4:42 PM. 9/30/2018
 * @Modify By:
 */
@Service
public class ReaderService implements IReaderService {

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    public IReaderDao iReaderDao;

    /**
     * Ordinary Pagination
     * @param currPage
     * @param pagesize
     * @return
     */
    @Override
    @Transactional(readOnly = true)  // 只读事务
    public Page<Reader> getPage(Integer currPage, Integer pagesize) {
        Pageable pageable = PageRequest.of(currPage - 1, pagesize);
        return readerRepository.findAll(pageable);
    }

    /**
     * Paging sorting desc by reader_id
     * @param currPage
     * @param pagesize
     * @return
     */
    @Override
    @Transactional(readOnly = true)  // 只读事务
    public Page<Reader> getPageSort(Integer currPage, Integer pagesize) {
        Sort sort = new Sort(Sort.Direction.DESC,"readerId");
        Pageable pageable = PageRequest.of(currPage-1, pagesize, sort);
        return readerRepository.findAll(pageable);
    }

    /**
     *@Title: ReaderService.java
     *@Params: string
     *@Return: List<Reader>
     *@Author: 毛文杰
     *@Description:
     */
    @Override
    public List<Reader> searchReaderByPhoneOrName(String string) {
        return iReaderDao.searchReaderByPhoneOrName(string);
    }

    /**
     *@Title: ReaderService.java
     *@Params: readerId
     *@Return: Integer
     *@Author: 毛文杰
     *@Description: find borrow count of the reader now
     *@Date: 3:33 PM. 10/12/2018
     */
    @Override
    public Integer findBorrowCountByReaderId(String readerId) {
        return iReaderDao.getBorrowCountByReaderId(readerId);
    }
}
