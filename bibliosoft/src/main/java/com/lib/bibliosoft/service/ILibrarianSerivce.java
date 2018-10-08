package com.lib.bibliosoft.service;


import com.lib.bibliosoft.entity.Comment;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 10:34 AM. 10/4/2018
 * @Modify By:
 */
public interface ILibrarianSerivce {

    Librarian findByLibrarianName(String loginname);

    List<Comment> findAllMessageForLibrarian();

    List<Feedback> findAllFeedbackForLibrarian();
}
