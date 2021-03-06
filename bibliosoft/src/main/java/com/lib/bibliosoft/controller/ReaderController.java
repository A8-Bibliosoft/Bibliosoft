package com.lib.bibliosoft.controller;

import com.alibaba.fastjson.JSON;
import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.*;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.repository.*;
import com.lib.bibliosoft.service.IReaderService;
import com.lib.bibliosoft.service.impl.BookSortService;
import com.lib.bibliosoft.utils.FileNameUtil;
import com.lib.bibliosoft.utils.FileUtil;
import com.lib.bibliosoft.utils.SendEmail;
import com.lib.bibliosoft.utils.VerifyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

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
    @Autowired
    private BulletinRepository bulletinRepository;
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ReaderController.class);

    /*page*/
    private Integer pagesize = 6;

    /**/
    private Integer totalCount;

    @Value("${spring.mail.username}")
    String emailusername;
    @Value("${spring.mail.password}")
    String emailpassword;
    @Value("${spring.mail.host}")
    String emailhost;

    /**
     * 后台首页的数据展示页面，网站统计
     * @title ReaderController.java
     * @param model
     * @return java.lang.String
     * @author 毛文杰
     * @method name lgoHome
     * @date 6:48 PM. 10/25/2018
     */
    @GetMapping("/home")
    public String lgoHome(Model model){
        Integer totalReaders = readerRepository.findAll().size();
        model.addAttribute("totalReaders", totalReaders);
        model.addAttribute("totalBooks", bookRepository.findAll().size());
        model.addAttribute("totalBooks", bookRepository.findAll().size());
        model.addAttribute("feedbacks", feedbackRepository.findFeedbacksByIsView("no"));
        model.addAttribute("totalfeedbacks", feedbackRepository.findAll().size());
        Integer deposit = defSettingRepository.findById(2).get().getDefnumber();
        float debt = 0;
        //找出所有的欠款的
        for(BorrowRecord borrowRecord : borrowRecordRepository.findAllIncome()){
            debt+=borrowRecord.getDebt();
        }
        //先这样算总收入
        float income = deposit*totalReaders + debt;
        model.addAttribute("income", income);
        return "home";
    }

    /**
     * home主页图表
     * @title ReaderController.java
     * @param response
     * @return void
     * @author 毛文杰
     * @method name showDataChart
     * @date 9:16 PM. 10/25/2018
     */
    @PostMapping("/homedata")
    @ResponseBody
    public void showDataChart(HttpServletResponse response){
        List<String> legend = new ArrayList<String>(Arrays.asList(new String[] { "all books", "on the shelf", "borrowed", "damaged","buying", "reserved"}));

        List<Integer> data = new ArrayList<>();
        List<Series<Integer>> series = new ArrayList<Series<Integer>>();

        //12个月
        for(int i=1;i<13;i++){
            data.add(bookRepository.findBookNumByMonth(2018, i).size());
        }
        series.add(new Series("all books","bar", data));

        data = new ArrayList<>();
        for(int i=1;i<13;i++){
            data.add(bookRepository.findBookNumByMonthandStatus(2018, i, 0).size());
        }
        series.add(new Series("on the shelf","bar",data));
        data = new ArrayList<>();
        for(int i=1;i<13;i++){
            data.add(bookRepository.findBookNumByMonthandStatus(2018, i, 1).size());
        }
        series.add(new Series("borrowed","bar",data));
        data = new ArrayList<>();
        for(int i=1;i<13;i++){
            data.add(bookRepository.findBookNumByMonthandStatus(2018, i, 2).size());
        }
        series.add(new Series("damaged","bar",data));
        data = new ArrayList<>();
        for(int i=1;i<13;i++){
            data.add(bookRepository.findBookNumByMonthandStatus(2018, i, 3).size());
        }
        series.add(new Series("buying","bar",data));
        data = new ArrayList<>();
        for(int i=1;i<13;i++){
            data.add(bookRepository.findBookNumByMonthandStatus(2018, i, 4).size());
        }
        series.add(new Series("reserved","bar",data));

        Echarts<Integer> echarts = new Echarts(legend, series);
        //解决中文乱码
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out;
        try{
            out = response.getWriter();
            String str=JSON.toJSONString(echarts);
            out.print(str);
            out.flush();
            out.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 总的书籍类型统计图表
     * @title ReaderController.java
     * @param response
     * @return void
     * @author 毛文杰
     * @method name showbooktypeDataChart
     * @date 15:22 AM. 11/3/2018
     */
    @PostMapping("/alltypedata")
    @ResponseBody
    public void showbooktypeDataChart(HttpServletResponse response){
        List<String> legend = new ArrayList<>();
        List<Series<BookNum>> series = new ArrayList<>();
        List<BookNum> bookNumList = new ArrayList<>();
        Integer totalnum;
        //找出所有书籍类型
        for(BookType s : bookTypeRepository.findAll()){
            totalnum = 0;
            //logger.info("书籍类型分别为={}",s.getTypeName());
            legend.add(s.getTypeName());
            //按照书籍类型找到isbn，然后查处每个isbn书籍的数目，求和，即是一个类型的总数
            for(Integer i : bookSortRepository.findBookNumByBookType(s.getTypeId())){
                totalnum+=i;
            }
            BookNum bookNum = new BookNum(totalnum, s.getTypeName());
            bookNumList.add(bookNum);
        }
        Series<BookNum> sbn = new Series<>("Number of books", "pie", bookNumList);
        series.add(sbn);
        //这里的series只有一个
        Echarts<BookNum> echarts = new Echarts(legend, series);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out;
        try{
            out = response.getWriter();
            String str=JSON.toJSONString(echarts);
            out.print(str);
            out.flush();
            out.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    //
    private void updateReaderAlldebt(String readerId){
        List<BorrowRecord> borrowRecordDebtList= borrowRecordRepository.findNowDebtByReaderId(readerId);
        float NowAllDebt=0;
        if(borrowRecordDebtList.size()>0){
            for(BorrowRecord x:borrowRecordDebtList){
                NowAllDebt+=x.getDebt();
            }
        }
        readerRepository.updateReaderNowDebt(readerId,NowAllDebt);
    }

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

        //logger.info("currpage={}",currpage);
//        List<Reader> list = new ArrayList<>();
//        while(readerIterator.hasNext()) {
//            list.add(readerIterator.next());
//        }
        //logger.info("list.size = {}",readerList.size());
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
    @GetMapping("reader_show/{id}")
    public String show_reader(@PathVariable("id") String readerId, Model model){
        //查询出这个reader的详细信息
//        logger.info(readerId);
        Reader reader = iReaderDao.findByReaderId(readerId);
        model.addAttribute("reader", reader);
        //找出借了几本书
        Integer borrownum = iReaderService.findBorrowCountByReaderId(readerId);
        model.addAttribute("num", borrownum);
//        logger.info(reader.getImgsrc());
        return "reader_show";
    }

    /**
     * librarian delete a reader
     * @param id
     * @return String
     */
    @PostMapping("/delete")
    @ResponseBody
    public String delete_reader(String id){
        Integer Iid = Integer.parseInt(id);
        //找到这个读者
        Reader reader = iReaderDao.findById(Iid);
        /*判断能否删除此读者*/
        //欠款未还清
        if("OFF".equals(reader.getStatus())){
            return ResultEnum.CANNOT_DEL_READER.getMsg();
        }else if(borrowRecordRepository.findNowDebtByReaderId(reader.getReaderId()).size()>0){
            return ResultEnum.READER_DEL_FAILED.getMsg();
        }else if(borrowRecordRepository.findByReaderIdAndReturntimeIsNull(reader.getReaderId()).size()>0){
            return ResultEnum.READER_DEL_CANCEL.getMsg();
        }else{
            //考虑要不要直接删掉，还是把状态改为DEL
            //iReaderDao.deleteReader(reader);
            iReaderDao.updateReaderStatusById(Iid, "DEL");
            logger.info("delete reader>>>  id={}, name={}",id, reader.getReaderName());
            return ResultEnum.READER_DEL_SUCCESS.getMsg();
        }
    }

    /**
     * add/edit a reader in the layer
     * @param readerId
     * @param readerName
     * @param sex
     * @param phone
     * @param email
     * @return String
     */
    @PostMapping("add_reader")
    public String reader_add(String readerId,String readerName,
                             @RequestParam("form-field-radio") String sex,
                             String phone, String email, String password,
                             @RequestParam("form-field-radio1") String status, String flag){
        if (flag.equals("edit")){
            Integer id = readerRepository.findReaderByReaderId(readerId).getId();
            //id是主键
            iReaderDao.updateReader(id, sex, readerName, phone, readerId, email, status, password,new Date());
        }else if(flag.equals("add")){
            //如果数据库里面存在之前标记为del的用户,则把它激活即可
            Reader reader1 = readerRepository.findReaderByReaderId(readerId);
            if(reader1 != null){
                 readerRepository.updateReaderStatusById(reader1.getId(),"ON");
                 Integer id = reader1.getId();
                 //更新他的信息
                 iReaderDao.updateReader(id, sex, readerName, phone, readerId, email, status, password,new Date());
                 return "redirect:/reader_list";
            }
            //之前不存在新建读者
            Reader reader = new Reader();
            reader.setSex(sex);
            reader.setReaderName(readerName);
            reader.setPhone(phone);
            reader.setReaderId(readerId);
            reader.setEmail(email);
            reader.setStatus(status);
            reader.setPassword(password);
            reader.setRegistTime(new Date());
            reader.setAlldebt(0);
            iReaderDao.addReader(reader);
            //logger.info("Add reader={}", reader.toString());
        }
        return "redirect:reader_list";
    }

    /**
     * modify reader's Status to OFF
     * @param id
     * @param model
     * @return
     */
    @PostMapping("reader_stop")
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
    @PostMapping("reader_start")
    @ResponseBody
    public String start_reader(String id, Model model){
        Integer Iid = Integer.parseInt(id);
        logger.info("OFF >>> ON");
        String s = Objects.requireNonNull(readerRepository.findById(Iid).orElse(null)).getStatus();

        iReaderDao.updateReaderStatusById(Iid, "ON");
        Reader reader=readerRepository.findById(Iid).get();
        reader.setAlldebt(0);
        //如果是del状态就更新注册日期
        if (s.equals("DEL"))
            reader.setRegistTime(new Date());
        readerRepository.save(reader);
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
    @GetMapping("reader_page")
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
    @PostMapping("reader_pageSort")
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
    @RequestMapping("reader_serach")
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
    @RequestMapping("goLogin")
    public String goLogin(Model model) throws Exception{
        return "ReaderLogin";
    }

    @PostMapping("reader_login")
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
                logger.info("login success");
                //默认头像
                if(reader.getImgsrc()==null||reader.getImgsrc().equals("")){
                    reader.setImgsrc("/static/readerimages/defaultimg.jpg");
                    readerRepository.save(reader);
                }
                //状态确认
                if(reader.getStatus().equals("DEL")){
                    return "delreader";
                }
                if(reader.getStatus().equals("ON")){
                    if(reader.getAlldebt()>0){
                        reader.setStatus("OFF");
                        readerRepository.save(reader);
                    }
                }
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
    @RequestMapping("goReaderInfo")
    public String goReaderInfo(Model model,HttpServletRequest request) throws Exception{
        HttpSession session=request.getSession();
        String readerId=null;
        if(session.getAttribute("readerId")!=null){
            readerId=session.getAttribute("readerId").toString();
            updateReaderAlldebt(readerId);
            List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
            List<AppointmentRecord> appointmentRecordList=appointmentRecordRepository.findByReaderId(readerId);
            List<BorrowRecord> historyRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNotNull(readerId);
            model.addAttribute("reader",readerRepository.findReaderByReaderId(readerId));
            model.addAttribute("borrowlist",borrowRecordList);
            model.addAttribute("appointmentlist",appointmentRecordList);
            model.addAttribute("historylist",historyRecordList);
            return "ReaderInfo";
        }

        return "redirect:goLogin";
    }

    @RequestMapping("changeReaderInfo")
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
            if(reader.getImgsrc()!=null&&!reader.getImgsrc().equals("")){
                String oldimgrc=reader.getImgsrc().substring(21);
                //logger.info(upload.getAbsolutePath()+"\\"+oldimgrc);
                if(!oldimgrc.equals("defaultimg.jpg")){
                    File oldImg=new File(upload.getAbsolutePath()+"\\"+oldimgrc);
                    if(oldImg.delete()){logger.info("删除原图片成功");}else{logger.info("删除原图片失败");}
                }
            }
            // 上传成功或者失败的提示
            String newfilename=FileNameUtil.getFileName(imgFile.getOriginalFilename());
            logger.info(newfilename);
            FileUtil.upload2(imgFile, upload.getAbsolutePath(), newfilename);
            logger.info(newfilename);
            String imgsrc="/static/readerimages/"+newfilename;
            readerRepository.updateReaderBasic(readerId,sex,readerName,imgsrc);
        }else{
            readerRepository.updateReaderBasic(readerId,sex,readerName);
        }
        return "redirect:goReaderInfo";
    }
    //修改邮箱
    //发送验证码
    @RequestMapping("sendEmailCode")
    @ResponseBody
    public String sendEmailCode(HttpSession session) throws Exception{
        String readerId=session.getAttribute("readerId").toString();
        if(readerId!=null){
            String emailcode=VerifyCode.getRandomCode(5);
            Reader reader=readerRepository.findReaderByReaderId(readerId);
            session.setAttribute("emailcode",emailcode);
            String message="Bibliosoft-A8 官方验证邮件！\n"
                    + "Hello, this is a email from Bibliosoft for you to get code.\n"
                    + "Your Code:" + emailcode
                    + "\n\n\n\n"
                    + "Thinks, from Librarian";
            SendEmail.sendMessage(emailhost,emailusername,emailpassword,message,reader.getEmail());
            logger.info(emailcode);
            return "success";
        }else{
            return "default";
        }
    }

    //修改邮箱
    @RequestMapping("changeEmail")
    @ResponseBody
    public String changeEmail(HttpSession session,String emailcode,String newemail) throws Exception{
        if(session.getAttribute("emailcode")!=null&&session.getAttribute("readerId")!=null&&emailcode!=null&&!emailcode.equals("")&&newemail!=null&&!newemail.equals("")){
            String nowemailcode=session.getAttribute("emailcode").toString();
            String readerId=session.getAttribute("readerId").toString();
            if(!nowemailcode.equals(emailcode)){
                return "erremailcode";
            }else{
                readerRepository.updateReaderEmail(readerId,newemail);
                session.removeAttribute("emailcode");
                return "success";
            }
        }
        return "error";
    }
    //忘记密码
    @RequestMapping("forgetPassword")
    @ResponseBody
    public String forgetPassword(String readerId) throws Exception{
        if(readerId!=null&&!readerId.equals("")){
            if(readerRepository.findReaderByReaderId(readerId)!=null){
                Reader reader=readerRepository.findReaderByReaderId(readerId);
                if(reader.getEmail()!=null&&reader.getPassword()!=null){
                    String message="Bibliosoft-A8 官方验证邮件！\n"
                            + "Hello, this is a email from Bibliosoft for you to get back your password.\n"
                            + "Your Password:" + reader.getPassword() + "\n"
                            + "Please take care of your password to prevent it from being stolen by others."
                            + "\n\n\n\n"
                            + "Thinks, from Librarian";
                    SendEmail.sendMessage(emailhost,emailusername,emailpassword,message,reader.getEmail());
                    return "success";
                }
            }
           return "errreaderId";
        }
        return "error";
    }
    //修改密码
    @RequestMapping("changePassword")
    @ResponseBody
    public String changePassword(HttpSession session,String oldpassword,String newpassword) throws Exception{
        if(session.getAttribute("readerId")!=null&&oldpassword!=null&&!oldpassword.equals("")&&newpassword!=null&&!newpassword.equals("")){
            String readerId=session.getAttribute("readerId").toString();
            Reader reader=readerRepository.findReaderByReaderId(readerId);
            if(!oldpassword.equals(reader.getPassword())){
                return "erroldpassword";
            }else{
                readerRepository.updateReaderPassword(readerId,newpassword);
                return "success";
            }
        }
        return "error";
    }

    @RequestMapping(value={"goHomePage", "/"})
    public String goHomePage(Model model) throws Exception{
        if(bulletinRepository.findHMNotices().size()>0&&bookSortRepository.findHMBook().size()>0){
            List<Notices> noticesList=bulletinRepository.findHMNotices();
            List<BookSort> bookSortList=bookSortRepository.findHMBook();
            model.addAttribute("noticelist",noticesList);
            model.addAttribute("booksortlist",bookSortList);
        }
        return "HomePage";
    }

    @RequestMapping("goSearch")
    public String goSearch(Model model,Integer booktypeid,Integer currpage) throws Exception{
        if(booktypeid!=null&&currpage!=0){
            Integer totalPages =  bookSortService.PageBook(0,pagesize,booktypeid).getTotalPages();
            model.addAttribute("totalpages", totalPages);
            //获得每页的数据
            if(currpage >0)
                currpage = currpage-1;
            List<BookSort> bookSortList=bookSortService.PageBook(currpage,pagesize,booktypeid).getContent();
            if(totalPages>1){
                model.addAttribute("currpage",currpage+1);
            }else{
                model.addAttribute("currpage",0);
            }
            model.addAttribute("booklist",bookSortList);
            model.addAttribute("booktypeid",booktypeid);
            model.addAttribute("booktypelist",bookTypeRepository.findAll());
        }else{
            model.addAttribute("currpage",0);
            model.addAttribute("booktypelist",bookTypeRepository.findAll());
        }
        return "Search";
    }


    @RequestMapping("search")
    public String search(Model model,String find_type,String find_info,Integer booktypeid) throws Exception{
        model.addAttribute("booktypelist",bookTypeRepository.findAll());
        model.addAttribute("currpage",0);
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

    @RequestMapping("goBookDetail")
    public String goBookDetail(Model model,String bookIsbn){
        List<Book> booklist=bookRepository.findBookByBookIsbn(bookIsbn);
        if(booklist.size()>0){
            model.addAttribute("book",booklist.get(0));
            model.addAttribute("bookStatus1",bookRepository.countAllByBookStatusAndBookIsbn(1,booklist.get(0).getBookIsbn()));
            model.addAttribute("bookStatus4",bookRepository.countAllByBookStatusAndBookIsbn(4,booklist.get(0).getBookIsbn()));
            model.addAttribute("bookStatus0",bookRepository.countAllByBookStatusAndBookIsbn(0,booklist.get(0).getBookIsbn()));
            model.addAttribute("allbook",booklist);
            return "BookDetail";
        }
        return "HomePage";
    }

    @RequestMapping("goOut")
    public String goOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:goHomePage";
    }

    //预约书籍
    @RequestMapping("bookBook")
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

        String readerId=session.getAttribute("readerId").toString();
        updateReaderAlldebt(readerId);
        //已借书籍+已预约书籍<最大可借书籍 剩余书籍大于0 用户未欠款(或状态为on)
        Book book=bookRepository.findByBookId(bookId);
        List<Book> reminebooklist=bookRepository.findByBookStatusAndBookIsbn(0,book.getBookIsbn());
        Reader reader=readerRepository.findReaderByReaderId(readerId);
        List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
        List<AppointmentRecord> appointmentRecordList=appointmentRecordRepository.findByReaderId(readerId);
        if(reader!=null&&borrowRecordList!=null&&appointmentRecordList!=null){
            if(reader.getAlldebt()==0&&reader.getStatus().equals("ON")){
                if(reminebooklist.size()>0){
                    if((borrowRecordList.size()+appointmentRecordList.size())<maxborrow.getDefnumber()){
                        appointmentRecordRepository.insertAppointment(reminebooklist.get(0).getBookId(),readerId,defSetting.getDefnumber());
                        bookRepository.updateBookStatus(4,reminebooklist.get(0).getBookId());
                        return "success";
                    }else{
                        return "maxborrow";
                    }
                }else{
                    return "default";
                }
            }else{
                return "lockreader";
            }
        }else{
            return "error";
        }
    }

    //读者借书
    @RequestMapping("goBorrowBook")
    public String goBorrowBook(){
        return "BorrowBook";
    }

    //借书，返回提示信息
    @RequestMapping("borrowBook")
    @ResponseBody
    public synchronized String borrowBook(String readerId,Integer bookId){

        //获取借书期限  借书前提 读者未被封禁(未欠款) 借书少于3本 且书还在架上
        Reader reader=readerRepository.findReaderByReaderId(readerId);
        //不存在读者
        if(reader == null) {
            return ResultEnum.READER_NOT_EXIST.getMsg();
        }
        //不存在书籍
        if(bookRepository.findByBookId(bookId) == null){
            return ResultEnum.NOT_EXIST.getMsg();
        }
        //OFF代表没有借书权限，直接不允许
        if("OFF".equals(reader.getStatus())){
            return ResultEnum.CANNOT_BORROW.getMsg();
        }else if(reader.getStatus().equals("ON")&&reader.getAlldebt()==0&&bookRepository.findByBookId(bookId)!=null&&readerRepository.findReaderByReaderId(readerId)!=null){
            //书籍在架上或者书籍被该读者预约
            if(bookRepository.getBookStatusByBookId(bookId)==0||appointmentRecordRepository.findbook(bookId)!=null){
                List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
                //获取最大借书数量
                DefSetting maxborrow=defSettingRepository.findDefSettingById(5);
                //logger.info("{}",borrowRecordList.size());
                if(borrowRecordList.size()<maxborrow.getDefnumber()){
                    DefSetting defSetting=defSettingRepository.findDefSettingById(3);
                    bookRepository.updateBookStatus(1,bookId);
                    //logger.info("{}",bookId);
                    borrowRecordRepository.insertBorrow(bookId,new Date(),defSetting.getDefnumber(),readerId);
                    //将预约的书籍记录删除
                    appointmentRecordRepository.clearbook(bookId);
                    return ResultEnum.BORROW_BOOK_SUCCESS.getMsg();
                }
            }
            return ResultEnum.HAVE_NO_BOOKS.getMsg();
        }
        return ResultEnum.BORROW_BOOK_ERROR.getMsg();
    }

    //读者还书页面
    @RequestMapping("goReturnBook")
    public String goReturnBook(){
        return "ReturnBook";
    }

    //还书 书籍存在 借书记录存在 书籍欠款为0
    @RequestMapping("returnBook")
    @ResponseBody
    public synchronized String returnBook(String bookid){
        Integer bookId = Integer.parseInt(bookid);
        BorrowRecord borrowRecord=borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookId);
        //判断记录是否存在
        if(borrowRecord == null){
            return ResultEnum.BORROW_RECORD_NOT_EXIST.getMsg();
        }
        String readerId=borrowRecord.getReaderId();
        updateReaderAlldebt(readerId);
        float debtmoney = 0;
        if(borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookId) != null){
            debtmoney = borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookId).getDebt();
        }else{
            return ResultEnum.BOOK_ALREADY_RETURN.getMsg();
        }
        if(bookRepository.findByBookId(bookId)!=null&&borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookId)!=null&&debtmoney==0){
            bookRepository.updateBookStatus(0,bookId);
            //已考虑重复问题，查找为归还的书 returntime is null
            borrowRecordRepository.updateBorrow(new Date(),0,bookId);
            //logger.info(borrowRecordRepository.findByBookId(bookId).toString());
            return ResultEnum.RETURN_BOOK_SUCCESS.getMsg();
        }else if(debtmoney>0){
            //在此还款
            bookRepository.updateBookStatus(0,bookId);
            borrowRecordRepository.updateBorrow(new Date(),0,bookId);
            //读者状态恢复为ON
            //readerRepository.updateReaderStatusById(Integer.parseInt(readerId),"ON");
            return ResultEnum.RETURN_BOOK_PAY.getMsg()+":"+debtmoney;
        }else{
            return ResultEnum.UNKNOWN_ERROR.getMsg();
        }
    }

    /**
     * @description: 当损坏或者丢失的时候需要缴纳的金额
     * @param: [bid]
     * @return: java.lang.String
     * @auther: 毛文杰
     * @date: 11/14/2018 11:10 PM
     */
    @PostMapping("bookreturn_calculatefine")
    @ResponseBody
    public String return_calculatefine(String bid){
        Pattern pattern = Pattern.compile("[0-9]*");
        if(!pattern.matcher(bid).matches()){
            return "null";
        }
        Integer bookid = Integer.parseInt(bid);
        Book book = bookRepository.findByBookId(bookid);
        if(book == null){
            return "null";
        }
        float price = book.getBookPrice();
        BorrowRecord borrowRecord = borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookid);
        float f = 0;
        if(borrowRecord != null) {
            f = borrowRecord.getDebt();
        }
        float money = price+f;
//        String msg = "You have been overdue for " + f + " days.";
//        return "As the books are damaged / lost, you need to pay " +money+ " yuan for it."+msg;
        return String.valueOf(money);
    }


    /**
     * 书籍损坏\丢失 缴纳罚款
     * @title ReaderController.java
     * @param bookid, fine
     * @return java.lang.String
     * @author 毛文杰
     * @method name payfine
     * @date 12:13 PM. 11/10/2018
     */
    @Transactional
    @PostMapping("pay_fine")
    @ResponseBody
    public String payfine(String bookid, String fine){
        Integer bookId = Integer.parseInt(bookid);
        float ffine = Float.parseFloat(fine);
        //1查找此书在book表是否存在
        if(bookRepository.findByBookId(bookId) == null){
            return ResultEnum.NOT_EXIST.getMsg();
        }
        //2查找此bookid是否在bookrecord表中存在,如果不存在返回提示
        if(borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookId) == null){
            return ResultEnum.BORROW_RECORD_NOT_EXIST.getMsg();
        }
//       else if(borrowRecordRepository.findByBookIdAndReturntimeIsNull(bookId) == null){
//            //书籍已还,无须再还
//            return ResultEnum.BOOK_ALREADY_RETURN.getMsg();
//        }

        //3将书籍的状态标志为已损坏\丢失(2)---->
        bookRepository.updateBookStatus(2,bookId);

        //4将罚款金额附加到record的debt中
        borrowRecordRepository.updateDebtByBookid(ffine, bookId);

        //5将returntime填进去,则book里面的allDebt会自动更新
        borrowRecordRepository.updateBorrow(new Date(),0, bookId);
        return ResultEnum.PAY_FINE_SUCCESS.getMsg();
    }


    //计时测试 还书期限
    //第一层returntime!=null 第二层lastday>0 第三层?
    @Scheduled(cron = "*/10 * * * * *")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void testCron1() throws MessagingException {
        //日期减少
        borrowRecordRepository.minusLastday();
        //增加欠款
        borrowRecordRepository.addDebt();

        // 所有欠款记录
        List<BorrowRecord> borrowRecordDebtList=borrowRecordRepository.findByDebt();
        if(borrowRecordDebtList.size()>0){
            for(BorrowRecord borrowRecord:borrowRecordDebtList){
                updateReaderAlldebt(borrowRecord.getReaderId());
                readerRepository.updateReaderStatusByReaderId(borrowRecord.getReaderId(),"OFF");
            }
        }

        //7天限制
        List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByLastday();
        if(borrowRecordList.size()>0){
            logger.info("发邮件啦!!");
            for(int i=0;i<borrowRecordList.size();i++){
                String readerId=borrowRecordList.get(i).getReaderId();
                String bookname=borrowRecordList.get(i).getBook().getBookName();
                Reader reader=readerRepository.findReaderByReaderId(readerId);
                String message="Bibliosoft-A8 官方验证邮件！\n"
                        + "Hello, this is a email from Bibliosoft for you to return book.\n"
                        + "Your book " + bookname + " must return in 7 days!\n"
                        + "Please return the book quickly!."
                        + "\n\n\n\n"
                        + "Thinks, from Librarian";
                SendEmail.sendMessage(emailhost,emailusername,emailpassword,message,reader.getEmail());
            }
        }
        //logger.info("还书日期减一天");
    }

    //预约时间
//    @Scheduled(cron = "0 0 */1 * * ?")  //cron接受cron表达式，根据cron表达式确定定时规则
    @Scheduled(cron = "*/20 * * * * *")
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
     * 胡皓
     */
    @RequestMapping("goFeedBack")
    public String goFeedBack(){
        return "FeedBack";
    }

    @RequestMapping("feedBack")
    @ResponseBody
    public String feedBack(HttpSession session,String content){
        if(session.getAttribute("readerId")!=null){
            String readerId=session.getAttribute("readerId").toString();
            Feedback feedback=new Feedback();
            feedback.setContent(content);
            feedback.setDate(new Date());
            feedback.setReaderId(readerId);
            feedback.setIsView("no");
            feedbackRepository.save(feedback);
            feedbackRepository.updateReaderIdById(feedback.getId(),readerId);
            return "success";
        }else{
            return "unlogin";
        }
    }

    /**
     * @title ReaderController.java
     * @param model 默认进入的页面模型展示所有未读的反馈荐购，分页paging
     * @return java.lang.String
     * @author 毛文杰
     * @method name gotoFeedbackPage
     * @date 12:18 PM. 10/20/2018
     */
    @GetMapping("reader_feedback")
    public String gotoFeedbackPage(Model model){
        Integer currpage = 1;
        totalCount = feedbackRepository.findFeedbacksByIsView("no").size();
//        if(totalCount == 0)
//            model.addAttribute("currpage",0);
        model.addAttribute("totalcount", totalCount);
        Integer totalPages = (totalCount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
        List<Feedback> feedbacks = iReaderService.findFeedbackCriteria(currpage-1, pagesize, "no").getContent();
        model.addAttribute("feedback", feedbacks);
        model.addAttribute("flag","yes");
        if(feedbacks.size()==0)
            model.addAttribute("currpage",0);
        else{
            model.addAttribute("currpage",currpage);
        }
        return "reader_feedback";
    }

    /**
     * 动态分页查询接受参数
     * @title ReaderController.java
     * @param currpage, model
     * @return java.lang.String
     * @author 毛文杰
     * @method name page_feedback
     * @date 10:44 PM. 10/20/2018
     */
    @GetMapping("feedback_page")
    public String page_feedback(@RequestParam(value = "currpage") Integer currpage,
                                @RequestParam(value = "flag") String flag, Model model){
        List<Feedback> feedbacks = null;
        if("yes".equals(flag)){
            totalCount = feedbackRepository.findFeedbacksByIsView("yes").size();
            model.addAttribute("totalcount", totalCount);
            Integer totalPages = (totalCount + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
            if(currpage == 0)
                currpage = 1;
            if(currpage == totalPages+1)
                currpage = totalPages;
            feedbacks = iReaderService.findFeedbackCriteria(currpage-1, pagesize, "yes").getContent();
        }else if ("no".equals(flag)){
            totalCount = feedbackRepository.findFeedbacksByIsView("no").size();
            model.addAttribute("totalcount", totalCount);
            Integer totalPages = (totalCount + pagesize - 1)/pagesize;
            model.addAttribute("totalpages", totalPages);
            if(currpage == 0)
                currpage = 1;
            if(currpage == totalPages+1)
                currpage = totalPages;
            feedbacks = iReaderService.findFeedbackCriteria(currpage-1, pagesize, "no").getContent();
        }

        //获得每页的数据
//        Sort sort = new Sort(Sort.Direction.DESC,"date");
//        Pageable pageable = PageRequest.of(currpage - 1, pagesize, sort);
//        List<Feedback> feedbacks = feedbackRepository.findAll(pageable).getContent();

        logger.info("currpage={}",currpage);
        logger.info("list.size = {}",feedbacks.size());
        //放在model
        model.addAttribute("feedback", feedbacks);
        model.addAttribute("currpage",currpage);
        model.addAttribute("flag", flag);
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
    @PostMapping("feedback/{id}")
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
    @PostMapping("setviewfeedback/{id}")
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
    @GetMapping("viewed_feedback")
    public String showViewedFeedbacks(Model model){
        Integer currpage = 1;
        totalCount = feedbackRepository.findFeedbacksByIsView("yes").size();
        model.addAttribute("totalcount", totalCount);
        Integer totalPages = (totalCount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
        List<Feedback> feedbacks = iReaderService.findFeedbackCriteria(currpage-1, pagesize, "yes").getContent();
        model.addAttribute("feedback", feedbacks);
        model.addAttribute("flag","yes");
        if(feedbacks.size()==0)
            model.addAttribute("currpage",0);
        else{
            model.addAttribute("currpage",currpage);
        }
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
    @GetMapping("unviewed_feedback")
    public String showUnviewedFeedbacks(Model model){
        Integer currpage = 1;
        totalCount = feedbackRepository.findFeedbacksByIsView("no").size();
        model.addAttribute("totalcount", totalCount);
        Integer totalPages = (totalCount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
        List<Feedback> feedbacks = iReaderService.findFeedbackCriteria(currpage-1, pagesize, "no").getContent();
        model.addAttribute("feedback", feedbacks);
        model.addAttribute("flag","no");
        if(feedbacks.size()==0)
            model.addAttribute("currpage",0);
        else{
            model.addAttribute("currpage",currpage);
        }
        return "reader_feedback";
    }
    /*-----------------------------结束---------------------------------*/
}
