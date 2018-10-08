package com.lib.bibliosoft.DAO.impl;

import com.lib.bibliosoft.DAO.ILibrarianDao;
import com.lib.bibliosoft.entity.Comment;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.repository.CommentRepository;
import com.lib.bibliosoft.repository.FeedbackRepository;
import com.lib.bibliosoft.repository.LibrarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 10:39 AM. 10/4/2018
 * @Modify By:
 */
@Component
public class LibrarianDao implements ILibrarianDao {

    @Autowired
    private LibrarianRepository librarianRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    /**
     * find a librarian by his name
     * @param loginname
     * @return
     */
    @Override
    public Librarian findByLiararianName(String loginname) {
        return librarianRepository.findByLibName(loginname);
    }

    /**
     * find all message by date, desc sorting
     * @return List
     */
    @Override
    public List<Comment> findAllMessageForLibrarian() {
        Sort sort = new Sort(Sort.Direction.DESC,"date");
        return commentRepository.findAll(sort);
    }

    @Override
    public List<Feedback> findAllFeedbackForLibrarian() {
        Sort sort = new Sort(Sort.Direction.DESC,"date");
        return feedbackRepository.findAll(sort);
    }
}
