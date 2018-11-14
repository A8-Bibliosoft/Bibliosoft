package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.BorrowRecord;
import com.lib.bibliosoft.entity.Feedback;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.BorrowRecordRepository;
import com.lib.bibliosoft.repository.DefSettingRepository;
import com.lib.bibliosoft.repository.LibrarianRepository;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private ILibrarianSerivce iLibrarianSerivce;

    private ReaderRepository readerRepository;

    private DefSettingRepository defSettingRepository;

    private BorrowRecordRepository borrowRecordRepository;

    private LibrarianRepository librarianRepository;

    /**
     * @description: 把注入信息统一包装到构造方法中去
     * @param: [iLibrarianSerivce, readerRepository, defSettingRepository, borrowRecordRepository, librarianRepository]
     * @auther: 毛文杰
     * @date: 11/14/2018 9:47 AM
     */
    @Autowired
    public LibrarianController(ILibrarianSerivce iLibrarianSerivce, ReaderRepository readerRepository,DefSettingRepository defSettingRepository, BorrowRecordRepository borrowRecordRepository,LibrarianRepository librarianRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.defSettingRepository = defSettingRepository;
        this.readerRepository = readerRepository;
        this.librarianRepository = librarianRepository;
        this.iLibrarianSerivce = iLibrarianSerivce;
    }

    /**
     * index Page of Librarian used system
     *
     * @return
     */
    @RequestMapping("lib_index")
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
     *  @param libid 图书馆员的id号码用来登录,这涉及到找回密码要重用董航的代码
     * @param password
     * @param code
     * @param request
     * @param response
     * @return Code indicating the result of the login
     * @throws IOException
     */
    @PostMapping("librarian_login")
    @ResponseBody
    public String loginReader(String libid,
                              String password,
                              String code,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        HttpSession session = request.getSession();
        //Automatically generated verify code
        String key = session.getAttribute(VerifyCode.RANDOMCODEKEY).toString();
        //set the type of response
        response.setContentType("text/plain;charset=UTF-8");

//        String loginname = librarianRepository.findByLibId(libid).getLibName();
//        Librarian librarian = iLibrarianSerivce.findByLibrarianName(loginname);

        Librarian librarian = librarianRepository.findByLibId(libid);

        if (!code.equals(key)) {
            logger.info("codeerror");
            return "codeerror";
        } else if (librarian == null) {
            logger.info("This userid={} does not exist", libid);
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
    @GetMapping("librarian_info")
    public String info_lib(Model model, HttpSession session) {
        if (session.getAttribute("librarian") != null) {
            Librarian lib = (Librarian) session.getAttribute("librarian");
            model.addAttribute("lib", lib);
            return "librarian_info";
        } else
            return "forward:lib_login";
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
    @PostMapping("income_sbday")
    public String sbday_totalincome(String day, Model model){
        int deposit=0;
        float fine=0;
        Date date = Date.valueOf(day);
        //logger.info("date={}",date);
        List<Reader> readerList = readerRepository.searchByRegistTimeandStatusnotDel(date,"DEL");
        //找出没有被注销的用户
        for(Reader r : readerList)
                logger.info("reader的状态为：{}", r.getStatus());

        //获取这一天注册了多少个新读者，乘以押金
        if(defSettingRepository.findById(2).isPresent())
            deposit = defSettingRepository.findById(2).get().getDefnumber()*readerList.size();
        model.addAttribute("deposit", deposit);
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByReturntime(date);
        for(BorrowRecord b : borrowRecords)
            fine+=b.getDebt();
        model.addAttribute("fine", fine);
        return "librarian_income";
    }

    /**
     * 获取某一周的收入，包括罚金和押金收入
     * @title LibrarianController.java
     * @param week, model
     * @return java.lang.String
     * @author huhao
     * @method name sbweek_totalincome
     * @date 11:55 AM. 11/6/2018
     */
    @PostMapping("income_sbweek")
    public String sbweek_totalincome(String week, Model model) throws Exception{
        int deposit=0;
        float fine=0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal=Calendar.getInstance();
        java.util.Date time=sdf.parse(week);
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date startdate=Date.valueOf(sdf.format(cal.getTime()));
        //System.out.println("所在周星期日的日期："+startdate);
        cal.add(Calendar.DATE, 6);
        Date enddate=Date.valueOf(sdf.format(cal.getTime()));
        //System.out.println("所在周星期六的日期："+enddate);

        //获取这一天注册了多少个新读者，乘以押金
        List<Reader> readerList = readerRepository.findByWeekandStatusnotDel(startdate,enddate,"DEL");
        if (defSettingRepository.findById(2).isPresent())
            deposit = defSettingRepository.findById(2).get().getDefnumber()*readerList.size();
        model.addAttribute("deposit", deposit);
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByWeek(startdate,enddate);
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
    @PostMapping("income_sbmonth")
    public String sbmonth_totalincome(String month, Model model){
        int deposit = 0;
        float fine=0;
        String []m = month.split("-");
        List<Reader> readerList = readerRepository.findByMonthRegistTimeandStatusnotDel(m[0],m[1],"DEL");
        logger.info("reader size",readerList.size());

        //获取这一天注册了多少个新读者，乘以押金
        if(defSettingRepository.findById(2).isPresent())
            deposit = defSettingRepository.findById(2).get().getDefnumber()*readerList.size();
        model.addAttribute("deposit", deposit);
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findByYearAndMonthReturntime(m[0],m[1]);
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
    public String lib_logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "forward:lib_login";
    }

    /**
     * 图书馆员自己修改信息
     * @title LibrarianController.java
     * @param libid, libname, phone, email
     * @return java.lang.String
     * @author 毛文杰
     * @method name modifyinfo
     * @date 10:34 PM. 11/9/2018
     */
    @PostMapping("lib_modify_info")
    public String modifyinfo(String libid, String libname, String phone, String email, HttpSession session){
        //logger.info("={}={}={}={}",libname,libid,email,phone);
        librarianRepository.updateLibrarianWithoutPass(libid,libname,email,phone);
        session.setAttribute("librarian", librarianRepository.findByLibId(libid));
        return "redirect:librarian_info";
    }

}
