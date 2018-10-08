package com.lib.bibliosoft.service.impl;

import com.lib.bibliosoft.DAO.ILibrarianDao;
import com.lib.bibliosoft.entity.Comment;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.service.ILibrarianSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 10:34 AM. 10/4/2018
 * @Modify By:
 */
@Service
public class LibrarianSerivce implements ILibrarianSerivce {

    @Autowired
    private ILibrarianDao iLibrarianDao;

    /**
     * find a librarian by his name
     * @param loginname
     * @return
     */
    @Override
    public Librarian findByLibrarianName(String loginname) {
        return iLibrarianDao.findByLiararianName(loginname);
    }

    /**
     * find All Comments
     * @return
     */
    @Override
    public List<Comment> findAllMessageForLibrarian() {
        return iLibrarianDao.findAllMessageForLibrarian();
    }

    /**
     * find all Feedbacks
     * @return
     */
    @Override
    public List<Feedback> findAllFeedbackForLibrarian() {
        return iLibrarianDao.findAllFeedbackForLibrarian();
    }
}
