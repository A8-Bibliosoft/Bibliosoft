package com.lib.bibliosoft.controller;

import com.lib.bibliosoft.entity.Book;
import com.lib.bibliosoft.entity.BookDelRecord;
import com.lib.bibliosoft.entity.BookSort;
import com.lib.bibliosoft.entity.Librarian;
import com.lib.bibliosoft.enums.ResultEnum;
import com.lib.bibliosoft.repository.*;
import com.lib.bibliosoft.service.impl.BookService;
import com.lib.bibliosoft.utils.BarcodeUtil;
import com.lib.bibliosoft.utils.FileNameUtil;
import com.lib.bibliosoft.utils.FileUtil;
import com.lib.bibliosoft.utils.ScanerIsbn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *@Title: BookController.java
 *@Author: 毛文杰
 *@Description: Book Controller
 *@Date: 10:07 PM. 10/6/2018
 */
@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookDelRecordRepository bookDelRecordRepository;

    @Autowired
    private BookSortRepository bookSortRepository;

    @Autowired
    private BookStatusRepository bookStatusRepository;

    @Autowired
    private BookTypeRepository bookTypeRepository;

    private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    /*共查询出的数目*/
    private Integer totalcount;

    /*每页的大小*/
    private Integer pagesize = 6;

    /**
     * Test
     * add a book
     * @return Result<Book>
     */
//    @PostMapping("/books")
//    @ResponseBody
//    public Result<Book> bookAdd(@Valid Book book, BindingResult bindingResult){
//        if (bindingResult.hasErrors()){
//            return ResultUtil.error(1, bindingResult.getFieldError().getDefaultMessage());
//        }
//        return ResultUtil.success(bookRepository.save(book));
//    }

    /**
     * Test
     * find a book by id
     * @param id
     * @return
     */
    @GetMapping("/books/{id}")
    @ResponseBody
    public Book bookFindOne(@PathVariable("id") Integer id){
        return bookRepository.findById(id).orElse(null);
        //the return statement also can write like this:
        //return bookRepository.findById(id).get();
        //When the method does not receive a parameter or the parameter is illegal, an error will be reported.
    }

    /**
     * Test
     * update a book's position by id
     * @param id the book's id
     * @param position book's position
     * @return a Book
     */
    @PutMapping("/books/{id}")
    @ResponseBody
    public Book updateBook(@PathVariable("id") Integer id,
                           @RequestParam("bookPosition") String position){
        Book book = bookRepository.findById(id).get();
        book.setBookPosition(position);
        return bookRepository.save(book);
    }

    /**
     * @title  BookController.java
     * @param id book's id not bookid
     * @return ResponseEntity type
     * @author  毛文杰
     * @description delete a book by it's id
     * @date  6:10 PM. 10/7/2018
     */
    @Transactional
    @PostMapping("/book/{id}")
    public ResponseEntity<Map<String,Object>> bookDelete(@PathVariable("id") Integer id, HttpSession session){

        /*先在删除记录表里记录*/
        BookDelRecord bookDelRecord = new BookDelRecord();
        Librarian librarian = (Librarian) session.getAttribute("librarian");
        Integer bookid = bookRepository.findById(id).get().getBookId();
        bookDelRecord.setBookId(bookid);
        bookDelRecord.setLibId(librarian.getLibId());
        bookDelRecord.setTime(new java.util.Date());
        bookDelRecordRepository.save(bookDelRecord);
        /*然后删除book表，这里用改变状态替代删除*/
//        bookRepository.deleteById(id);
        /*5: 已删除，但没从数据库删除记录*/
        bookRepository.updateBookStatus(5, bookid);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msg", ResultEnum.SUCCESS.getMsg());
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * @title: BookController.java
     * @param: isbn
     * @return: List<Book>
     * @author: 毛文杰
     * @date: 11:14 PM. 9/27/2018
     * @modifyTime 8:14 PM. 10/12/2018
     */
    @GetMapping("/book_sBybisbn")
    public String findBooksByIsbn(@RequestParam("isbn") String isbn, Model model){
        List<Book> books = bookRepository.findBookByBookIsbn(isbn);
        logger.info("bookisbn={}", isbn);
        Integer totalcount =  books.size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);
        model.addAttribute("books",books);
        if(totalcount == 0)
            model.addAttribute("currpage",0);
        else
            model.addAttribute("currpage",1);
        return "book_list";
    }

    /**
     * Test
     * get the price of a book，Throw the exception to the ExceptionHandler for processing
     * @param id
     */
//    @GetMapping("/books/getPrice/{id}")
//    @ResponseBody
//    public void getPrice(@PathVariable("id") Integer id) throws Exception {
//        bookService.getPrice(id);
//    }

    /**
     * list all the books
     * @param model
     * @return
     */
    @GetMapping("/book_list")
    public String list_book(Model model){
        Integer currpage = 1;
        totalcount = bookService.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        //获得每页的数据
        List<Book> bookList = bookService.getPage(currpage, pagesize).getContent();

        logger.info("currpage={}",currpage);
//        List<Book> list = new ArrayList<>();
//        while(bookIterator.hasNext()) {
//            list.add(bookIterator.next());
//        }
//        logger.info("list.size = {}",list.size());
//        logger.info("list[0]={}", list.get(0));
        //放在model
        model.addAttribute("books", bookList);
        model.addAttribute("currpage",currpage);
        return "book_list";
    }

    /**
     * paging list book
     * @param currpage
     * @param model
     * @return
     */
    @GetMapping("/book_page")
    public String page_book(@RequestParam(value = "currpage") Integer currpage, Model model){
        totalcount = bookService.findAll().size();
        model.addAttribute("totalcount", totalcount);
        Integer totalPages = (totalcount + pagesize - 1)/pagesize;
        model.addAttribute("totalpages", totalPages);

        if(currpage == 0)
            currpage = 1;
        if(currpage == totalPages+1)
            currpage = totalPages;
        //获得每页的数据
        List<Book> bookList = bookService.getPage(currpage, pagesize).getContent();

        logger.info("currpage={}",currpage);
//        List<Book> list = new ArrayList<>();
//        while(bookIterator.hasNext()) {
//            list.add(bookIterator.next());
//        }
//        logger.info("list.size = {}",list.size());
        //放在model
        model.addAttribute("books", bookList);
        model.addAttribute("currpage",currpage);
        return "book_list";
    }

    /**
     * search by bookname or ISBN or add time
     * @param model
     * @param bookname
     * @param bookaddtime
     * @return
     */
    @RequestMapping("/book_search")
    public String search_book(Model model, @RequestParam("bookname") String bookname, @RequestParam("bookaddtime") String bookaddtime){
        logger.info("book name==={}, book add time==={}",bookname, bookaddtime);
        List<Book> searchBook = null;
        try {
            if (bookService.searchBookByNameOrAddTime(bookname, bookaddtime) != null){
                searchBook = bookService.searchBookByNameOrAddTime(bookname, bookaddtime);
                model.addAttribute("totalcount", searchBook.size());
                Integer totalPages = (searchBook.size() + pagesize - 1)/pagesize;
                model.addAttribute("totalpages", totalPages);
                model.addAttribute("currpage",1);
            }else {
                model.addAttribute("totalcount", 0);
                model.addAttribute("totalpages", 0);
                model.addAttribute("currpage",0);
            }

        } catch (ParseException e) {
            logger.error("Parse Date Error={}", e);
            e.printStackTrace();
        }

        //logger.info("查询结果===大小size={}",searchBook.size());
        model.addAttribute("books",searchBook);
        return "book_list";
    }

    /**
     *@Title: BookController.java
     *@Params: bookid, model
     *@Return: String
     *@Author: 毛文杰
     *@Description: find a book by its bookid
     *@Date: 5:22 PM. 10/12/2018
     */
    @GetMapping("/book_sBybid")
    public String searchBookByBookid(String bookid, Model model){
        Integer ibookid = Integer.parseInt(bookid);
//        List<Book> books = bookRepository.findBookByBookIsbn(sbookid);
        Book books = bookRepository.findBookByBookId(ibookid);
        logger.info("bookid={}", bookid);
        if(books != null){
            model.addAttribute("totalcount", 1);
            model.addAttribute("totalpages", 1);
            model.addAttribute("currpage",1);
        }else{
            model.addAttribute("totalcount", 0);
            model.addAttribute("totalpages", 0);
            model.addAttribute("currpage",0);
        }
        model.addAttribute("books",books);
        return "book_list";
    }



    /**
     * edit a book's info in the layer
     * @param bookId
     * @param bookName
     * @param bookPosition
     * @param isbn
     * @param price
     * @param author
     * @param status
     * @param flag
     * @return
     */
    @PostMapping("/edit_book")
    public String book_edit(Integer bookId,String bookName,String bookPosition,
                             String isbn, String price, String author,
                             @RequestParam("form-field-radio1") String status, String flag){
        float fprice = Float.parseFloat(price);
        Integer istatus = Integer.parseInt(status);
        if (flag.equals("edit")){
            Integer id = bookRepository.findBookByBookId(bookId).getId();
            bookService.updateBook(id, bookName, bookPosition, isbn, bookId, fprice, author, istatus);
        }else if(flag.equals("add")){
            Book book = new Book();
            book.setBookId(bookId);
            book.setBookIsbn(isbn);
            book.setBookPrice(fprice);
            book.setBookName(bookName);
            book.setBookPosition(bookPosition);
            book.setBookAuthor(author);
            book.setBookStatus(istatus);
            bookService.addBook(book);
            logger.info("Add book={}",book.toString());
        }
        return "redirect:/book_list";
    }

    /**
     * @Title: BookController.java
     * @param booktitle
     * @param bookcover
     * @param bookisbn
     * @param bookauthor
     * @param bookposition
     * @param bookprice
     * @param bookid
     * @param bookstatus
     * @param bookaddtime
     * @param booksummary
     * @return "success" mean that the book is added to database successfully
     * @Author: 毛文杰
     * @Description: add a new book to the library
     * @Date: 10:07 PM. 10/6/2018
     */
    @Transactional
    @RequestMapping("/book_addnewbook")
    public ResponseEntity<Map<String,Object>> add_newbook(String booktitle, MultipartFile bookcover, String bookisbn, String bookauthor,
                                                          String bookposition, String bookprice, String bookid, String bookstatus, String bookaddtime,
                                                          String booksummary, String typeid){
        Map<String,Object> map = new HashMap<String,Object>();

        /*书籍*/
        Book book = new Book();
        Integer Ibookstatus = Integer.parseInt(bookstatus);
        book.setBookStatus(Ibookstatus);
        book.setBookAuthor(bookauthor);
        book.setBookPosition(bookposition);
        book.setBookName(booktitle);
        float Fbookprice = Float.parseFloat(bookprice);
        book.setBookPrice(Fbookprice);
        book.setBookIsbn(bookisbn);

        Integer Ibookid = Integer.parseInt(bookid);
        if (bookRepository.findByBookId(Ibookid)!= null)
            book.setBookId(Ibookid);
        else{
            //bookid不能重复，若已存在则返回错误信息！
            map.put("msg", ResultEnum.ADD_BOOK_FAILED.getMsg());
            return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_ACCEPTABLE);
        }

        //java.sql.Date
        Date date = Date.valueOf(bookaddtime);
        book.setRegisterTime(date);
        book.setBookDesc(booksummary);

        /*获得新的文件名*/
        String bookImg = FileNameUtil.getFileName(bookcover.getOriginalFilename());
        book.setBookImg(bookImg);

        if(bookSortRepository.findByBookIsbn(bookisbn) != null){
            logger.info("已有ISBN编号为==={}的书籍，故不插入", bookisbn);
        }else{
            //书籍分类表
            BookSort bookSort = new BookSort();
            bookSort.setBookAuthor(bookauthor);
            bookSort.setBookIsbn(bookisbn);
            bookSort.setBookName(booktitle);
            bookSort.setTypeId(Integer.parseInt(typeid));
            //关联
            bookSort.getBookList().add(book);
            book.setBookSort(bookSort);
        }

        //保存
        bookService.addBook(book);

        // 要上传的目标文件存放路径为 static/bookimages/
        File p = null;
        try {
            p = new File(ResourceUtils.getURL("classpath:").getPath());
            //logger.info(p.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File upload = new File(p.getAbsolutePath(),"static/bookimages/");
        //logger.info(upload.getAbsolutePath());
        if(!upload.exists())
            upload.mkdirs();
        // 上传成功或者失败的提示
        String msg;

        /*这是获取项目根目录*/
//        String ph = ClassUtils.getDefaultClassLoader().getResource("").getPath();
//        logger.info("path的绝对路径 = {}", ph);

        if (FileUtil.upload(bookcover, upload.getAbsolutePath(), bookcover.getOriginalFilename())){
            // 上传成功，给出页面提示
            msg = ResultEnum.ADD_BOOK_SUCCESS.getMsg();
        }else {
            msg = ResultEnum.ADD_BOOK_FAILED.getMsg();
        }

        // 显示图片
        map.put("msg", msg);
        //map.put("fileName", file.getOriginalFilename());
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    /**
     * @title: BookController.java
     * @param bookId
     * @param model
     * @return String -> view page
     * @author 毛文杰
     * @description show detail information of a book
     * @date 6:24 PM. 10/7/2018
     */
    @GetMapping("/book_show/{id}")
    public String show_book(@PathVariable("id") String bookId, Model model){
        Integer bookid = Integer.parseInt(bookId);
        Book book = bookService.getBookById(bookid);
        String sstatus = bookService.findsstatusByid(bookid);
        logger.info("书籍状态 === {}", sstatus);
        model.addAttribute("book", book);
        model.addAttribute("sstatus", sstatus);
        return "book_show";
    }

    /**
     * @param isbn
     * @param time
     * @param position
     * @param status
     * @return String
     * @title BookController.java
     * @author 毛文杰
     * @description add books by isbn
     * @date 2:19 PM. 10/9/2018
     */

    @PostMapping("/book_isbn")
    public ResponseEntity<Map<String,Object>> getInfoByDouBan(String isbn, String time, String position, String status, String num, String typeid){
        Map<String,Object> map = new HashMap<String,Object>();
        Integer number = Integer.parseInt(num);
        //get the books and improve their information
        List<Book> books = ScanerIsbn.getBookInfoByIsbn(isbn, number, time, position, status, typeid);

        StringBuilder bookids = new StringBuilder();
        try {
            for (Book b : books){
                bookService.addBook(b);
                logger.info(isbn);
                logger.info(""+b.getBookId());
                bookRepository.insertBookIsbn(isbn,b.getBookId());
                //生成条形码
                BarcodeUtil.generateFile(String.valueOf(b.getBookId()));
                //提示语句
                bookids.append(b.getBookId() + ";");
            }

        }catch(Exception e){
            logger.error("添加书籍出错！error = {}", e.getMessage());
            map.put("msg", ResultEnum.ADD_BOOK_FAILED.getMsg());
            e.printStackTrace();
            return new ResponseEntity<Map<String,Object>>(map, HttpStatus.valueOf(500));
        }
        map.put("msg", ResultEnum.ADD_BOOK_SUCCESS.getMsg() + " bookid: " + bookids);
        return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    }

    @GetMapping("/book_addByIsbn")
    public String goaddBookIsbnPage(Model model){
        model.addAttribute("types", bookTypeRepository.findAll());
        model.addAttribute("status", bookStatusRepository.findAll());
        return "book_addByIsbn";
    }

    @GetMapping("bookadd_detail")
    public String goAddBookpage(Model model){
        model.addAttribute("types", bookTypeRepository.findAll());
        model.addAttribute("status", bookStatusRepository.findAll());
        return "bookadd_detail";
    }
}

