package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.BorrowRecord;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.BorrowRecordRepository;
import com.lib.bibliosoft.repository.DefSettingRepository;
import com.lib.bibliosoft.repository.ReaderRepository;
import com.lib.bibliosoft.service.ILibrarianSerivce;
import com.lib.bibliosoft.utils.VerifyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
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

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private DefSettingRepository defSettingRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    /**
     * index Page of Librarian used system
     *
     * @return
     */
    @RequestMapping("/lib_index")
    @ResponseBody
    public ModelAndView gotoIndex() {
        ModelAndView mv = new ModelAndView("lib_index");
        //find unseen
//        List<Comment> commentList = iLibrarianSerivce.findAllMessageForLibrarian();
        //mv.addObject("commentList", commentList);
//        mv.addObject("commentNum", commentList.size());

        //find unseen
        List<Feedback> feedbackList = iLibrarianSerivce.findAllFeedbackForLibrarian();
        //mv.addObject("feedbackList", feedbackList);
        mv.addObject("feedbackNum", feedbackList.size());
        return mv;
    }

    /**
     * Librarian login the system he is using
     *
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

        if (!code.equals(key)) {
            logger.info("codeerror");
            return "codeerror";
        } else if (librarian == null) {
            logger.info("This username={} does not exist", loginname);
            return "usererror";
        } else if (password.equals(librarian.getPassword())) {
            logger.info("login success");
            //Put the info into the session
            session.setAttribute("librarian", librarian);
            session.setAttribute("logintype", "librarian");
            session.setAttribute("islogin", true);
            return "success";
        } else {
            logger.info("usererror");
            return "usererror";
        }
    }

    /**
     * go to info page
     *
     * @return
     */
    @GetMapping("/librarian_info")
    public String info_lib(Model model, HttpSession session) {
        if (session.getAttribute("librarian") != null) {
            Librarian lib = (Librarian) session.getAttribute("librarian");
            model.addAttribute("lib", lib);
            return "librarian_info";
        } else
            return "/lib_login";
    }

    /**
     * 获取某一天的收入，包括罚金和押金收入
     * @title LibrarianController.java
     * @param day, model
     * @return java.lang.String
     * @author 毛文杰
     * @method name sbday_totalincome
     * @date 11:55 AM. 11/4/2018
     */
    @PostMapping("/income_sbday")
    public String sbday_totalincome(String day, Model model){
        Integer deposit=0,fine=0;
        Date date = Date.valueOf(day);
        logger.info("date={}",date);
        List<Reader> readerList = readerRepository.findByRegistTime(date);

        //获取这一天注册了多少个新读者，乘以押金
        deposit = defSettingRepository.findById(2).get().getDefnumber()*readerList.size();
        model.addAttribute("deposit", deposit);
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByReturntime(date);
        for(BorrowRecord b : borrowRecords)
            fine+=b.getDebt();
        model.addAttribute("fine", fine);
        return "librarian_income";
    }

    /**
     * 通过月份搜索总收入
     * @title LibrarianController.java
     * @param month, model
     * @return java.lang.String
     * @author 毛文杰
     * @method name sbmonth_totalincome
     * @date 1:51 PM. 11/4/2018
     */
    @PostMapping("/income_sbmonth")
    public String sbmonth_totalincome(String month, Model model){
        Integer deposit=0,fine=0;
        String []m = month.split("-");
        List<Reader> readerList = readerRepository.findByMonthRegistTime(m[1]);
        //获取这一天注册了多少个新读者，乘以押金
        deposit = defSettingRepository.findById(2).get().getDefnumber()*readerList.size();
        model.addAttribute("deposit", deposit);
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByMonthReturntime(m[1]);
        for(BorrowRecord b : borrowRecords)
            fine+=b.getDebt();
        model.addAttribute("fine", fine);
        return "librarian_income";
    }

    /**
     * logout the system
     *
     * @param request
     * @param response
     * @return String
     */
    @GetMapping("lib_logout")
    public String lib_logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return "/lib_login";
    }

}
