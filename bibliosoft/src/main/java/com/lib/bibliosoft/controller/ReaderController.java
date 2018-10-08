package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.DAO.IReaderDao;
import com.lib.bibliosoft.entity.AppointmentRecord;
import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.entity.BorrowRecord;
import com.lib.bibliosoft.entity.Reader;
import com.lib.bibliosoft.repository.*;
import com.lib.bibliosoft.service.IReaderService;
import com.lib.bibliosoft.utils.VerifyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ReaderController.class);

    private Integer pagesize = 6;

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
        Iterator<Reader> readerIterator = iReaderService.getPage(currpage, pagesize).iterator();

        logger.info("currpage={}",currpage);
        List<Reader> list = new ArrayList<>();
        while(readerIterator.hasNext()) {
            list.add(readerIterator.next());
        }
        logger.info("list.size = {}",list.size());
        logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("readers", list);
        model.addAttribute("currpage",currpage);
        return "reader_list";
    }

    /**
     * show detail information of a reader
     * @param readerId
     * @param model
     * @return
     */
    @GetMapping("/reader_show/{id}")
    public String show_reader(@PathVariable("id") String readerId, Model model){
        Integer readerid = Integer.parseInt(readerId);
        Reader reader = iReaderDao.findByReaderId(readerid);
        model.addAttribute("reader", reader);
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
    public String reader_add(Integer readerId,String readerName,
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
        Iterator<Reader> readerIterator = iReaderService.getPage(currpage, pagesize).iterator();

        logger.info("currpage={}",currpage);
        List<Reader> list = new ArrayList<>();
        while(readerIterator.hasNext()) {
            list.add(readerIterator.next());
        }
        logger.info("list.size = {}",list.size());
        //logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("readers", list);
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
    public String readerLogin(Integer readerId, String password, String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Reader reader=null;
        if((reader=readerRepository.findByReaderId(readerId))!=null){
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
        Integer readerId=Integer.parseInt(session.getAttribute("readerId").toString());

        List<Book> borrowbooklist=new ArrayList<Book>();
        List<BorrowRecord> borrowRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNull(readerId);
        for(int i=0;i<borrowRecordList.size();i++){
            borrowbooklist.add(bookRepository.findByBookId(borrowRecordList.get(i).getBookId()));
        }

        List<Book> appointmentbooklist=new ArrayList<Book>();
        List<AppointmentRecord> appointmentRecordList=appointmentRecordRepository.findByReaderId(readerId);
        for(int i=0;i<appointmentRecordList.size();i++){
            appointmentbooklist.add(bookRepository.findByBookId(appointmentRecordList.get(i).getBookId()));
        }

        List<Book> historybooklist=new ArrayList<Book>();
        List<BorrowRecord> historyRecordList=borrowRecordRepository.findByReaderIdAndReturntimeIsNotNull(readerId);
        for(int i=0;i<historyRecordList.size();i++){
            historybooklist.add(bookRepository.findByBookId(historyRecordList.get(i).getBookId()));
        }



        model.addAttribute("reader",readerRepository.findByReaderId(readerId));
        model.addAttribute("borrowlist",borrowbooklist);
        model.addAttribute("appointmentlist",appointmentbooklist);
        model.addAttribute("historylist",historybooklist);
        return "ReaderInfo";
    }

    @RequestMapping("/goHomePage")
    public String goHomePage() throws Exception{
        return "HomePage";
    }

    @RequestMapping("/goSearch")
    public String goSearch(Model model,Integer booktypeid) throws Exception{
        model.addAttribute("booktypelist",bookTypeRepository.findAll());
        model.addAttribute("booklist",bookSortRepository.findByTypeId(booktypeid));
        return "Search";
    }
    @RequestMapping("/search")
    public String search(Model model,String find_type,String find_info,Integer booktypeid) throws Exception{
        model.addAttribute("booktypelist",bookTypeRepository.findAll());
        model.addAttribute("booklist",bookSortRepository.findByTypeId(booktypeid));
        switch(find_type){
            case "0":
                model.addAttribute("booklist",bookSortRepository.findByBookNameLike("%"+find_info+"%"));break;
            case "1":
                model.addAttribute("booklist", bookSortRepository.findByBookAuthorLike("%"+find_info+"%"));break;
            case "2":
                model.addAttribute("booklist",bookSortRepository.findByBookIsbn(find_info));break;
            default:
                model.addAttribute("booklist","Error!");
        }
        return "Search";
    }

    @RequestMapping("/goBookDetail")
    public String goBookDetail(Model model,Integer bookId){
        Book book=bookRepository.findByBookId(bookId);
        model.addAttribute("book",book);
        model.addAttribute("bookStatus1",bookRepository.countAllByBookStatusAndBookIsbn(1,book.getBookIsbn()));
        model.addAttribute("bookStatus4",bookRepository.countAllByBookStatusAndBookIsbn(4,book.getBookIsbn()));
        model.addAttribute("bookStatus0",bookRepository.countAllByBookStatusAndBookIsbn(0,book.getBookIsbn()));
        return "BookDetail";
    }

    @RequestMapping("/goOut")
    public String goOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:goHomePage";
    }

    @RequestMapping("/bookBook")
    public String bookBook(Integer bookId){
        bookRepository.updateBookStatus(4,bookId);
        return "redirect:goBookDetail?bookId="+bookId;
    }
}
