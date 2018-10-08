package com.lib.bibliosoft.DAO;

import com.lib.bibliosoft.entity.Comment;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;

import java.util.List;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 10:38 AM. 10/4/2018
 * @Modify By:
 */
public interface ILibrarianDao {

    Librarian findByLiararianName(String loginname);

    List<Comment> findAllMessageForLibrarian();

    List<Feedback> findAllFeedbackForLibrarian();
}
