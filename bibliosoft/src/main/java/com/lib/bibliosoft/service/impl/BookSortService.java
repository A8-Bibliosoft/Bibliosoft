package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.entity.BookSort;
import com.lib.bibliosoft.repository.BookSortRepository;
import com.lib.bibliosoft.service.IBookSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
/**
 * @title BookService.java
 * @return java.lang.String
 * @author huhao
 */
@Service
public class BookSortService implements IBookSortService {
    @Autowired
    private BookSortRepository bookSortRepository;
    @Override
    public Page<BookSort> PageBook (Integer page, Integer size,Integer typeId) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "typeId");
        Page<BookSort> bookSortPage=bookSortRepository.findAll(new Specification<BookSort>(){
            @Override
            public Predicate toPredicate(Root<BookSort> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate p1 = criteriaBuilder.equal(root.get("typeId").as(String.class),typeId);
                query.where(criteriaBuilder.and(p1));
                return query.getRestriction();
            }
        },pageable);
        return bookSortPage;
    }
}
