package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.*;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.repository.*;
import com.lib.bibliosoft.service.IReaderService;
import com.lib.bibliosoft.service.impl.BookSortService;
import com.lib.bibliosoft.utils.FileNameUtil;
import com.lib.bibliosoft.utils.FileUtil;
import com.lib.bibliosoft.utils.VerifyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @Author: 毛文杰
 * @Description:
 * @Date: Created in 4:34 PM. 9/30/2018
 * @Modify By:
 */
@Controller
public class ReaderController {

    @Autowired
    private IReaderService iReaderService;
    @Autowired
    private IReaderDao iReaderDao;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private BookSortRepository bookSortRepository;
    @Autowired
    private BookTypeRepository bookTypeRepository;
    @Autowired
    private AppointmentRecordRepository appointmentRecordRepository;
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    @Autowired
    private DefSettingRepository defSettingRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private BookSortService bookSortService;
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ReaderController.class);

    /*page*/
    private Integer pagesize = 6;

    /**/
    private Integer totalCount;

    /**
     * list all the reader
     * @param model
     * @return
     */
    @GetMapping("/reader_list")
    public String readerList(Model model){

        Integer currpage = 1;
        totalCount = iReaderDao.findAll().size();
        model.addAttribute("totalcount", totalCount);
        Integer totalPages = (totalCount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        //获得每页的数据
        List<Reader> readerList = iReaderService.getPage(currpage, pagesize).getContent();

        logger.info("currpage={}",currpage);
//        List<Reader> list = new ArrayList<>();
//        while(readerIterator.hasNext()) {
//            list.add(readerIterator.next());
//        }
        logger.info("list.size = {}",readerList.size());
        //logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("readers", readerList);
        model.addAttribute("currpage",currpage);
        return "reader_list";
    }

    /**
     * show detail information of a reader
     * @param readerId
     * @param model
     * @return string
     * @modify 毛文杰
     * @Date 2018-10-12 PM 15:29
     */
    @GetMapping("/reader_show/{id}")
    public String show_reader(@PathVariable("id") String readerId, Model model){
        //查询出这个reader的详细信息
        Reader reader = iReaderDao.findByReaderId(readerId);
        model.addAttribute("reader", reader);
        //找出借了几本书
        Integer borrownum = iReaderService.findBorrowCountByReaderId(readerId);
        model.addAttribute("num", borrownum);
        return "reader_show";
    }

    /**
     * librarian delete a reader
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String delete_reader(String id){
        Integer Iid = Integer.parseInt(id);
        Reader reader = iReaderDao.findById(Iid);
        iReaderDao.deleteReader(reader);
        logger.info("delete reader>>>  id={}",id);
        return "success";
    }

    /**
     * add/edit a reader in the layer
     * @param readerId
     * @param readerName
     * @param sex
     * @param phone
     * @param email
     * @return
     */
    @PostMapping("/add_reader")
    public String reader_add(String readerId,String readerName,
                             @RequestParam("form-field-radio") String sex,
                             String phone, String email,
                             @RequestParam("form-field-radio1") String status, String flag){
        if (flag.equals("edit")){
            Integer id = readerRepository.findReaderByReaderId(readerId).getId();
            iReaderDao.updateReader(id, sex, readerName, phone, readerId, email, status);
        }else if(flag.equals("add")){
            Reader reader = new Reader();
            reader.setSex(sex);
            reader.setReaderName(readerName);
            reader.setPhone(phone);
            reader.setReaderId(readerId);
            reader.setEmail(email);
            reader.setStatus(status);
            iReaderDao.addReader(reader);
            logger.info("Add reader={}", reader.toString());
        }
        return "redirect:/reader_list";
    }

    /**
     * modify reader's Status to OFF
     * @param id
     * @param model
     * @return
     */
    @PostMapping("/reader_stop")
    @ResponseBody
    public String stop_reader(String id, Model model){
        logger.info("id={}",id);
        Integer Iid = Integer.parseInt(id);
        logger.info("ON >>> OFF");
        iReaderDao.updateReaderStatusById(Iid, "OFF");
        logger.info("reader.status >>> ={}",iReaderDao.findById(Iid).getStatus());
        model.addAttribute("readers", iReaderDao.findAll());
        return Iid.toString();
    }

    /**
     * modify reader's Status to OFF
     * @param id
     * @param model
     * @return
     */
    @PostMapping("/reader_start")
    @ResponseBody
    public String start_reader(String id, Model model){
        Integer Iid = Integer.parseInt(id);
        logger.info("OFF >>> ON");
        iReaderDao.updateReaderStatusById(Iid, "ON");
        logger.info("reader.status >>> ={}",iReaderDao.findById(Iid).getStatus());
        model.addAttribute("readers", iReaderDao.findAll());
        return Iid.toString();
    }

    /**
     * show the reader pages by paging
     * @param currpage
     * @param model
     * @return
     */
    @GetMapping("/reader_page")
    public String page_reader(@RequestParam(value = "currpage") Integer currpage, Model model){

        totalCount = iReaderDao.findAll().size();
        model.addAttribute("totalcount", totalCount);
        Integer totalPages = (totalCount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        if(currpage == 0)
            currpage = 1;
        if(currpage == totalPages+1)
            currpage = totalPages;
        //获得每页的数据
        List<Reader> readerList = iReaderService.getPage(currpage, pagesize).getContent();

        logger.info("currpage={}",currpage);
//        List<Reader> list = new ArrayList<>();
//        while(readerIterator.hasNext()) {
//            list.add(readerIterator.next());
//        }
        logger.info("list.size = {}",readerList.size());
        //logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("readers", readerList);
        model.addAttribute("currpage",currpage);
        return "reader_list";
    }

    // 排序分页显示数据
    @PostMapping("/reader_pageSort")
    @ResponseBody
    public Page<Reader> showSortPage(@RequestParam(value = "currpage") Integer currpage, @RequestParam(value = "pagesize") Integer pagesize){
        logger.info("paging-sort >>> currpage= {}, pagesize= {}", currpage, pagesize);
        return iReaderService.getPageSort(currpage, pagesize);
    }

    /**
     * search reader by Phone or Reader_name
     * @param model
     * @param search_content
     * @return
     */
    @RequestMapping("/reader_serach")
    public String search_reader(Model model, @RequestParam("search_content") String search_content){
        logger.info("search_content==={}",search_content);
        List<Reader> searchReader = iReaderService.searchReaderByPhoneOrName(search_content);
        logger.info("查询结果===大小size={}",searchReader.size());
        model.addAttribute("readers",searchReader);
        //当前页写死了
        model.addAttribute("currpage",1);
        model.addAttribute("totalcount", searchReader.size());
        Integer totalPages = (searchReader.size() + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
        return "reader_list";
    }

    /**
     *@Author: huhao
     *@Description: test
     *@Date: 11:14 PM. 10/1/2018
     */
    @RequestMapping("/goLogin")
    public String goLogin(Model model) throws Exception{
        return "ReaderLogin";
    }

    @PostMapping("/reader_login")
    @ResponseBody
    public String loginReader(String readerId, String password, String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Reader reader=null;
        if((reader=readerRepository.findReaderByReaderId(readerId))!=null){
            HttpSession session = request.getSession();
            String key = session.getAttribute(VerifyCode.RANDOMCODEKEY).toString();
            response.setContentType("text/plain;charset=UTF-8");
            if (!code.equals(key)){
                logger.info("codeerror");
                return "codeerror";
            }
            else if(password.equals(reader.getPassword())){
                session.setAttribute("readerId",readerId);
                session.setAttribute("loginname",reader.getReaderName());
                session.setAttribute("logintype","reader");
                session.setAttribute("islogin", true);
                logger.info("login uccess");
                return "success";
            }
            else{
                logger.info("usererror");
                return "usererror";
            }
        }else{
            return "usererror";
        }
    }
    @RequestMapping("/goReaderInfo")
    public String goReaderInfo(Model model,HttpServletRequest request) throws Exception{
        HttpSession session=request.getSession();
        String readerId=null;
        if(session.getAttribute("readerId")!=null){
            readerId=session.getAttribute("readerId").toString();
            List<BorrowRecord> borrowRecordDebtList= borrowRecordRepository.findNowDebtByReaderId(readerId);
            Integer NowAllDebt=0;
            if(borrowRecordDebtList.size()>0){
                for(BorrowRecord x:borrowRecordDebtList){
                    NowAllDebt+=x.getDebt();
                }
            }
            readerRepository.updateReaderNowDebt(readerId,NowAllDebt);
            List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
            List<AppointmentRecord> appointmentRecordList=appointmentRecordRepository.findByReaderId(readerId);
            List<BorrowRecord> historyRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNotNull(readerId);
            model.addAttribute("reader",readerRepository.findReaderByReaderId(readerId));
            model.addAttribute("borrowlist",borrowRecordList);
            model.addAttribute("appointmentlist",appointmentRecordList);
            model.addAttribute("historylist",historyRecordList);
            //logger.info(borrowRecordList.get(0).getBook().getBookName());
           // logger.info(appointmentRecordList.get(0).getBook().getBookPosition());
            return "ReaderInfo";
        }

        return "redirect:goLogin";
    }

    @RequestMapping("/changeReaderInfo")
    public synchronized String changeReaderInfo (HttpServletRequest request,String readerName,String sex,MultipartFile imgFile){
        HttpSession session=request.getSession();
        String readerId=session.getAttribute("readerId").toString();
        if(!imgFile.isEmpty()){
            // 要上传的目标文件存放路径
            File p = null;
            try {
                p = new File(ResourceUtils.getURL("classpath:").getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File upload = new File(p.getAbsolutePath(),"static/readerimages/");
            if(!upload.exists())
                upload.mkdirs();

            //删除原图片
            Reader reader=readerRepository.findReaderByReaderId(readerId);
            if(reader.getImgsrc()!=null){
                String oldimgrc=reader.getImgsrc().substring(21);
                logger.info(upload.getAbsolutePath()+"\\"+oldimgrc);
                File oldImg=new File(upload.getAbsolutePath()+"\\"+oldimgrc);
                if(oldImg.delete()){logger.info("删除原图片成功");}else{logger.info("删除原图片失败");}
            }
            // 上传成功或者失败的提示
            String newfilename=FileNameUtil.getFileName(imgFile.getOriginalFilename());
            FileUtil.upload(imgFile, upload.getAbsolutePath(), newfilename);
            String imgsrc="/static/readerimages/"+newfilename;
            readerRepository.updateReaderBasic(readerId,sex,readerName,imgsrc);
        }else{
            readerRepository.updateReaderBasic(readerId,sex,readerName);
        }
        return "redirect:goReaderInfo";
    }

    @RequestMapping("/goHomePage")
    public String goHomePage() throws Exception{
        //logger.info(bookSortService.PageBook(0,5,1).getContent().toString());
        return "HomePage";
    }

    @RequestMapping("/goSearch")
    public String goSearch(Model model,Integer booktypeid,Integer currpage) throws Exception{
        if(booktypeid!=null){
            Integer totalPages =  bookSortService.PageBook(0,pagesize,booktypeid).getTotalPages();
            model.addAttribute("totalpages", totalPages);
            //获得每页的数据
            if(currpage == 1)
                currpage = 0;
            if(currpage == totalPages)
                currpage = totalPages-1;
            List<BookSort> bookSortList=bookSortService.PageBook(currpage,pagesize,booktypeid).getContent();
            model.addAttribute("currpage",currpage+1);
            model.addAttribute("booklist",bookSortList);
            model.addAttribute("booktypeid",booktypeid);
            model.addAttribute("booktypelist",bookTypeRepository.findAll());
        }else{
            model.addAttribute("currpage",0);
            model.addAttribute("booktypelist",bookTypeRepository.findAll());
        }
        //logger.info(bookSortRepository.findByTypeId(1).get(0).getOneBook().getBookPosition().getPlace());
        return "Search";
    }
    @RequestMapping("/search")
    public String search(Model model,String find_type,String find_info,Integer booktypeid) throws Exception{
        model.addAttribute("booktypelist",bookTypeRepository.findAll());
        model.addAttribute("booklist",bookSortRepository.findByTypeId(booktypeid));
        if(find_info!=null&&!find_info.equals("")) {
            switch (find_type) {
                case "0":
                    model.addAttribute("booklist", bookSortRepository.findByBookNameLike("%" + find_info + "%"));
                    break;
                case "1":
                    model.addAttribute("booklist", bookSortRepository.findByBookAuthorLike("%" + find_info + "%"));
                    break;
                case "2":
                    model.addAttribute("booklist", bookSortRepository.findByBookIsbn(find_info));
                    break;
                default:
                    model.addAttribute("booklist", "Error!");
            }
        }
        return "Search";
    }

    @RequestMapping("/goBookDetail")
    public String goBookDetail(Model model,String bookIsbn){
        List<Book> booklist=bookRepository.findBookByBookIsbn(bookIsbn);
        if(booklist.size()>0){
            model.addAttribute("book",booklist.get(0));
            model.addAttribute("bookStatus1",bookRepository.countAllByBookStatusAndBookIsbn(1,booklist.get(0).getBookIsbn()));
            model.addAttribute("bookStatus4",bookRepository.countAllByBookStatusAndBookIsbn(4,booklist.get(0).getBookIsbn()));
            model.addAttribute("bookStatus0",bookRepository.countAllByBookStatusAndBookIsbn(0,booklist.get(0).getBookIsbn()));
            return "BookDetail";
        }
        return "HomePage";
    }

    @RequestMapping("/goOut")
    public String goOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:goHomePage";
    }

    @RequestMapping("/bookBook")
    @ResponseBody
    public String bookBook(HttpServletRequest request,Integer bookId){
        HttpSession session=request.getSession();
        if(session.getAttribute("islogin")==null){
            return "unlogin";
        }
        //获取默认预约时间
        DefSetting defSetting=defSettingRepository.findDefSettingById(4);
        //获取最大借书数量
        DefSetting maxborrow=defSettingRepository.findDefSettingById(5);
        logger.info(defSetting.getDeftype());
        String readerId=session.getAttribute("readerId").toString();
        //已借书籍+已预约书籍<最大可借书籍 剩余书籍大于0 用户未欠款(或状态为on)
        Book book=bookRepository.findByBookId(bookId);
        List<Book> reminebooklist=bookRepository.findByBookStatusAndBookIsbn(0,book.getBookIsbn());
        Reader reader=readerRepository.findReaderByReaderId(readerId);
        List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
        List<AppointmentRecord> appointmentRecordList=appointmentRecordRepository.findByReaderId(readerId);
        if(reader.getAlldebt()==0&&reminebooklist.size()>0&&(borrowRecordList.size()+appointmentRecordList.size())<maxborrow.getDefnumber()){
            appointmentRecordRepository.insertAppointment(reminebooklist.get(0).getBookId(),readerId,defSetting.getDefnumber());
            bookRepository.updateBookStatus(4,reminebooklist.get(0).getBookId());
            return "success";
        }else {
            return "default";
        }
    }

    //读者借书
    @RequestMapping("/goBorrowBook")
    public String goBorrowBook(){
        return "BorrowBook";
    }

    @RequestMapping("/borrowBook")
    public synchronized String borrowBook(String readerId,Integer bookId){
        //获取借书期限  借书前提 读者未被封禁(未欠款) 借书少于3本 且书还在架上
        Reader reader=readerRepository.findReaderByReaderId(readerId);
        if(reader.getAlldebt()==0&&bookRepository.findByBookId(bookId)!=null&&readerRepository.findReaderByReaderId(readerId)!=null&&bookRepository.getBookStatusByBookId(bookId)==0){
            List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
            //获取最大借书数量
            DefSetting maxborrow=defSettingRepository.findDefSettingById(5);
            if(borrowRecordList.size()<maxborrow.getDefnumber()){
                DefSetting defSetting=defSettingRepository.findDefSettingById(3);
                bookRepository.updateBookStatus(1,bookId);
                borrowRecordRepository.insertBorrow(bookId,new Date(),defSetting.getDefnumber(),readerId);
                return null;
            }
            return null;
        }
       return null;
    }

    //读者还书
    @RequestMapping("/goReturnBook")
    public String goReturnBook(){
        return "ReturnBook";
    }
    //书籍存在 借书记录存在 书籍欠款为0
    @RequestMapping("/returnBook")
    public synchronized String returnBook(Integer bookId){
        if(bookRepository.findByBookId(bookId)!=null&&borrowRecordRepository.findByBookId(bookId)!=null&&borrowRecordRepository.findByBookId(bookId).getDebt()==0){
            bookRepository.updateBookStatus(0,bookId);
            //已考虑重复问题，查找为归还的书 returntime is null
            borrowRecordRepository.updateBorrow(new Date(),0,bookId);
            //logger.info(borrowRecordRepository.findByBookId(bookId).toString());
            return null;
        }
        return null;
    }

    //计时测试 还书期限
    //第一层returntime!=null 第二层lastday>0 第三层?
    @Scheduled(cron = "*/10 * * * * *")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void testCron1() {
        //日期减少
        borrowRecordRepository.minusLastday();
        //增加欠款
        borrowRecordRepository.addDebt();
        List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByLastday();
        if(borrowRecordList.size()>0){
            logger.info("发邮件啦!!");
        }
        //logger.info("还书日期减一天");
    }

    //预约时间
//    @Scheduled(cron = "0 0 */1 * * ?")  //cron接受cron表达式，根据cron表达式确定定时规则
    @Scheduled(cron = "*/10 * * * * *")
    public void testCron2() {
        //时间减少
        appointmentRecordRepository.minusLasttime();
        //改变到期的书籍状态
        List<AppointmentRecord> appointmentRecordList=appointmentRecordRepository.findAllEnd();
        if(appointmentRecordList.size()>0){
            for(AppointmentRecord x:appointmentRecordList){
                bookRepository.updateBookStatus(0,x.getBookId());
            }
        }
        //清除记录
        appointmentRecordRepository.clearLasttime();
        //logger.info("预约时间减一小时");
    }



    /**
     *@Title: ReaderController.java
     *@Params: readerid
     *@Return: String
     *@Author: 毛文杰
     *@Description:
     *@Date: 4:01 PM. 10/12/2018
     */
    @GetMapping("/borrowbook")
    public String gotoBorrowBookList(@RequestParam("readerid") String readerid, Model model){
        List<BorrowRecord> bookRecords = borrowRecordRepository.findAllByReaderId(readerid);
        model.addAttribute("borrowRecords", bookRecords);
        return "borrowBookList";
    }



    /*---------------------reader feedback module---------------------------*/

    /**
     * @title ReaderController.java
     * @param model 页面模型
     * @return java.lang.String
     * @author 毛文杰
     * @method name gotoFeedbackPage
     * @date 12:18 PM. 10/20/2018
     */
    @GetMapping("/reader_feedback")
    public String gotoFeedbackPage(Model model){
        model.addAttribute("feedback", feedbackRepository.findAll());
        return "reader_feedback";
    }

    /**
     * delete the feedback from reader
     * @title ReaderController.java
     * @param id identification
     * @return org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.Object>>
     * @author 毛文杰
     * @method name deleteFeedback
     * @date 12:26 PM. 10/20/2018
     */
    @PostMapping("/feedback/{id}")
    public ResponseEntity<Map<String,Object>> deleteFeedback(@PathVariable("id") Integer id){
        feedbackRepository.deleteById(id);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msg", ResultEnum.SUCCESS.getMsg());
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * @title ReaderController.java
     * @description set the status to viewed: yes
     * @return org.springframework.http.ResponseEntity<java.util.Map<java.lang.String,java.lang.Object>>
     * @author 毛文杰
     * @method name viewFeedback
     * @date 2:17 PM. 10/20/2018
     */
    @PostMapping("/setviewfeedback/{id}")
    public ResponseEntity<Map<String,Object>> viewFeedback(@PathVariable("id") Integer id){
        feedbackRepository.updateStatusById(id);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msg", ResultEnum.SUCCESS.getMsg());
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /***
     * find all feedbacks that had viewed
     * @title ReaderController.java
     * @return java.lang.String
     * @author 毛文杰
     * @method name showViewedFeedbacks
     * @date 2:22 PM. 10/20/2018
     */
    @GetMapping("/viewed_feedback")
    public String showViewedFeedbacks(Model model){
        model.addAttribute("feedback", feedbackRepository.findFeedbacksByIsView("yes"));
        return "reader_feedback";
    }

    /**
     * find all unviewed feedbacks
     * @title ReaderController.java
     * @return java.lang.String
     * @author 毛文杰
     * @method name showViewedFeedbacks
     * @date 2:23 PM. 10/20/2018
     */
    @GetMapping("/unviewed_feedback")
    public String showUnviewedFeedbacks(Model model){
        model.addAttribute("feedback", feedbackRepository.findFeedbacksByIsView("no"));
        return "reader_feedback";
    }
}
