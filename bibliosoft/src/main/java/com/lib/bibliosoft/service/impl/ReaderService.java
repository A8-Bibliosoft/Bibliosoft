package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.FeedbackRepository;
import com.lib.bibliosoft.repository.ReaderRepository;
import com.lib.bibliosoft.service.IReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    private FeedbackRepository feedbackRepository;

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

    /**
     * get a page of feedback by currpage, and sort desc according to date
     * @title ReaderService.java
     * @param [page, size, status]
     * @return org.springframework.data.domain.Page<com.lib.bibliosoft.entity.Feedback>
     * @author 毛文杰
     * @method name findFeedbackCriteria
     * @date 10:00 PM. 10/20/2018
     */
    @Override
    public Page<Feedback> findFeedbackCriteria(Integer page, Integer size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "date");
        //predicate:封装了单个的查询条件
        //root：查询对象的属性的封装
        //query：封装了执行的查询的信息
        //criteriaBuilder：查询条件的构造器，定义不同的查询条件
        Page<Feedback> feedbackPage = feedbackRepository.findAll(new Specification<Feedback>(){
            @Override
            public Predicate toPredicate(Root<Feedback> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                //参数一：查询的条件属性
                //参数二：条件的值
                Predicate p1 = criteriaBuilder.equal(root.get("isView"), status);
                query.where(criteriaBuilder.and(p1));
                return query.getRestriction();
            }
        },pageable);
        return feedbackPage;
    }
}
