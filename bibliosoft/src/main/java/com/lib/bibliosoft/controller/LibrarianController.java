package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.Comment;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.service.ILibrarianSerivce;
import com.lib.bibliosoft.utils.VerifyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


/**
 * @Author: 毛文杰
 * @Description: controller for librarian
 * @Date: Created in 10:21 AM. 10/4/2018
 * @Modify By: maowenjie
 */
@Controller
public class LibrarianController {

    //Logger
    private Logger logger = LoggerFactory.getLogger(LibrarianController.class);

    @Autowired
    private ILibrarianSerivce iLibrarianSerivce;

    /**
     * index Page of Librarian used system
     * @return
     */
    @RequestMapping("/lib_index")
    @ResponseBody
    public ModelAndView gotoIndex(){
        ModelAndView mv = new ModelAndView("lib_index");
        List<Comment> commentList = iLibrarianSerivce.findAllMessageForLibrarian();
        mv.addObject("commentList", commentList);
        mv.addObject("commentNum",commentList.size());

        List<Feedback> feedbackList = iLibrarianSerivce.findAllFeedbackForLibrarian();
        mv.addObject("feedbackList", feedbackList);
        mv.addObject("feedbackNum",feedbackList.size());
        return mv;
    }

    /**
     * Librarian login the system he is using
     * @param loginname
     * @param password
     * @param code
     * @param request
     * @param response
     * @return Code indicating the result of the login
     * @throws IOException
     */
    @PostMapping("/librarian_login")
    @ResponseBody
    public String loginReader(String loginname,
                              String password,
                              String code,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        //Automatically generated verify code
        String key = session.getAttribute(VerifyCode.RANDOMCODEKEY).toString();
        //set the type of response
        response.setContentType("text/plain;charset=UTF-8");

        Librarian librarian = iLibrarianSerivce.findByLibrarianName(loginname);

        if (!code.equals(key)){
            logger.info("codeerror");
            return "codeerror";
        }else if (librarian == null){
            logger.info("This username={} does not exist", loginname);
            return "usererror";
        }else if(password.equals(librarian.getPassword())){
            logger.info("login success");
            //Put the info into the session
            session.setAttribute("librarian", librarian);
            session.setAttribute("logintype","librarian");
            session.setAttribute("islogin", true);
            return "success";
        }else{
            logger.info("usererror");
            return "usererror";
        }
    }

    /**
     * go to info page
     * @return
     */
    @GetMapping("/librarian_info")
    public String info_lib(){
        return "librarian_info";
    }

    /**
     * logout the system
     * @param request
     * @param response
     * @return String
     */
    @GetMapping("lib_logout")
    public String lib_logout(HttpServletRequest request,HttpServletResponse response){
        request.getSession().invalidate();
        return "/lib_login";
    }


}
